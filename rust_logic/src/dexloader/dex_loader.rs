use jni::objects::JByteArray;
use jni::sys::jint;
use jni::JNIEnv;
#[cfg(unix)]
use std::ffi::CString;

// ashmem definitions
#[cfg(unix)]
const ASHMEM_DEVICE: &str = "/dev/ashmem";

// ioctl commands for ashmem
// We cast these to i32 at the use site to satisfy Android's libc definition of ioctl request
#[cfg(all(unix, target_pointer_width = "64"))]
const ASHMEM_SET_SIZE: u64 = 0x40087703;
#[cfg(all(unix, target_pointer_width = "32"))]
const ASHMEM_SET_SIZE: u32 = 0x40047703;

#[cfg(unix)]
const ASHMEM_SET_NAME: u64 = 0x41007701;

#[no_mangle]
#[allow(non_snake_case)]
pub extern "system" fn create_in_memory_dex(
    env: JNIEnv,
    _class: jni::objects::JObject,
    data: JByteArray,
) -> jint {
    let data_len = match env.get_array_length(&data) {
        Ok(len) => len as usize,
        Err(_) => return -1,
    };

    if data_len == 0 {
        return -1;
    }

    let data_bytes = match env.convert_byte_array(&data) {
        Ok(bytes) => bytes,
        Err(_) => return -1,
    };

    #[cfg_attr(not(unix), allow(unused_assignments))]
    let mut fd: i32 = -1;

    #[cfg(unix)]
    {
        // 1. Try memfd_create (available on kernel 3.17+ / API 26+)
        let name = CString::new("dex").unwrap();
        unsafe {
            // __NR_memfd_create values:
            // aarch64: 279, arm: 385, x86_64: 319, i386: 356
            #[cfg(target_arch = "aarch64")]
            const NR_MEMFD_CREATE: libc::c_long = 279;
            #[cfg(target_arch = "arm")]
            const NR_MEMFD_CREATE: libc::c_long = 385;
            #[cfg(target_arch = "x86_64")]
            const NR_MEMFD_CREATE: libc::c_long = 319;
            #[cfg(target_arch = "x86")]
            const NR_MEMFD_CREATE: libc::c_long = 356;
            #[cfg(not(any(
                target_arch = "aarch64",
                target_arch = "arm",
                target_arch = "x86_64",
                target_arch = "x86"
            )))]
            const NR_MEMFD_CREATE: libc::c_long = -1;

            if NR_MEMFD_CREATE != -1 {
                fd = libc::syscall(NR_MEMFD_CREATE, name.as_ptr(), 0) as i32;
            }
        }

        // 2. Fallback to ashmem
        if fd < 0 {
            let dev_path = CString::new(ASHMEM_DEVICE).unwrap();
            unsafe {
                fd = libc::open(dev_path.as_ptr(), libc::O_RDWR);
                if fd >= 0 {
                    // Set name
                    let mut name_to_set = [0u8; 256];
                    let dex_bytes = b"dex";
                    name_to_set[..dex_bytes.len()].copy_from_slice(dex_bytes);

                    // On Android, ioctl request is often i32
                    let set_name_cmd = ASHMEM_SET_NAME as i32;
                    if libc::ioctl(fd, set_name_cmd, name_to_set.as_ptr()) < 0 {
                        libc::close(fd);
                        fd = -1;
                    } else {
                        // Set size
                        let set_size_cmd = ASHMEM_SET_SIZE as i32;

                        if libc::ioctl(fd, set_size_cmd, data_len as libc::size_t) < 0 {
                            libc::close(fd);
                            fd = -1;
                        }
                    }
                }
            }
        }

        // 3. Write data
        if fd >= 0 {
            unsafe {
                let mut total_written = 0;
                let mut success = true;
                while total_written < data_len {
                    let written = libc::write(
                        fd,
                        data_bytes.as_ptr().add(total_written) as *const libc::c_void,
                        (data_len - total_written) as libc::size_t,
                    );

                    if written < 0 {
                        // On Android, errno in libc is usually accessed via __errno() returning *mut c_int
                        #[cfg(target_os = "android")]
                        let err = *libc::__errno();
                        #[cfg(not(target_os = "android"))]
                        let err = *libc::__errno_location();

                        if err == libc::EINTR {
                            continue;
                        }
                        libc::close(fd);
                        fd = -1;
                        success = false;
                        break;
                    }
                    total_written += written as usize;
                }

                if success {
                    // Reset position
                    if libc::lseek(fd, 0, libc::SEEK_SET) < 0 {
                        libc::close(fd);
                        fd = -1;
                    }
                }
            }
        }
    }

    #[cfg(not(unix))]
    {
        // Stub for non-unix environments
        let _ = data_bytes;
        let _ = data_len;
        fd = -1;
    }

    fd
}
