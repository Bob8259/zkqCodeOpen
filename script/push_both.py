import subprocess
import sys
from typing import Iterable


REMOTES: tuple[str, ...] = ("origin", "collaborate")


def run_git(args: Iterable[str]) -> subprocess.CompletedProcess[str]:
    return subprocess.run(
        ["git", *args],
        text=True,
        capture_output=True,
    )


def current_branch() -> str:
    result = run_git(["branch", "--show-current"])
    if result.returncode != 0:
        print(result.stderr.strip() or "Failed to detect current Git branch.")
        sys.exit(result.returncode)

    branch = result.stdout.strip()
    if not branch:
        print("Could not detect current Git branch. Detached HEAD is not supported.")
        sys.exit(1)

    return branch


def push_to_remote(remote: str, branch: str) -> None:
    # Always force-push to collaborate; keep origin as a normal push.
    force = remote == "collaborate"
    action = "Force-pushing" if force else "Pushing"
    print(f"{action} {branch} to {remote}...")
    cmd = ["git", "push", "--force", remote, branch] if force else ["git", "push", remote, branch]
    result = subprocess.run(cmd, text=True)
    if result.returncode != 0:
        print(f"Push to {remote} failed with exit code {result.returncode}.")
        sys.exit(result.returncode)


def main() -> None:
    branch = sys.argv[1] if len(sys.argv) > 1 else current_branch()

    for remote in REMOTES:
        push_to_remote(remote, branch)

    print(f"Pushed {branch} successfully to all configured repositories.")


if __name__ == "__main__":
    main()
