# V2compose Release Skill Plan

## Summary

Create `/Users/cooaer/.agents/skills/v2compose-release` as the release execution skill that runs after `v2compose-release-prep`.

The skill finishes a `release/vX.Y.Z` branch with git-flow style merges, pushes `master`, `develop`, and tags, waits for `.github/workflows/release.yml`, and verifies the GitHub Release APK assets.

## Implementation

- Initialize a new skill named `v2compose-release` under `/Users/cooaer/.agents/skills`.
- Write `SKILL.md` with the release finish workflow:
  - require a clean `release/vX.Y.Z` branch after release prep has been committed
  - merge into `master` with `git merge --no-ff`
  - create the annotated `vX.Y.Z` tag on the `master` merge commit
  - merge into `develop` with `git merge --no-ff`
  - delete the release branch locally and remotely after both merges succeed
  - push `master`, `develop`, and tags
  - wait for GitHub Actions and verify the GitHub Release assets
- Add `scripts/inspect_v2compose_publish.py` for read-only preflight checks.
## Validation

- Run the skill validator:

```bash
/Users/cooaer/.codex/skills/.system/skill-creator/scripts/quick_validate.py /Users/cooaer/.agents/skills/v2compose-release
```

- Run the preflight script from this repository. On non-release branches it should fail without modifying files and report that the current branch is not `release/vX.Y.Z`.
- Inspect `SKILL.md` to confirm both `master` and `develop` use `git merge --no-ff`.

## Assumptions

- Release publishing remains GitHub-based and is triggered by pushing `v*` tags.
- The release workflow requires the tag commit to be reachable from `origin/master`, so branch pushes occur before tag pushes.
