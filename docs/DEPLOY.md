# Publish beespace.live (GitHub → S3 → CloudFront)

**GitHub stores the site source.** **beespace.live** is served from **Amazon S3** behind **CloudFront**. Until this pipeline is configured, merging a pull request updates GitHub only — **not** the public website.

Grok Bot, Cursor, and other agents can edit the repo. **Only you** (via GitHub Actions secrets or manual AWS upload) can publish to the live bucket. **Do not paste AWS access keys into Grok Bot, Cursor chat, or commit them to the repo.**

---

## What “publish” means

| Step | Where | Result |
|------|--------|--------|
| 1. Merge PR to `main` | GitHub | Latest HTML/CSS/JS in repo |
| 2. GitHub Actions workflow runs | GitHub → AWS | Files synced to S3 bucket |
| 3. CloudFront invalidation | AWS | Visitors see new content |

Workflow file: [`.github/workflows/deploy-beespace-live.yml`](../.github/workflows/deploy-beespace-live.yml)

---

## One-time setup (recommended): GitHub Actions + OIDC

### A. Confirm your AWS resources

You need (you likely already have these for the current site):

1. **S3 bucket** — holds the site files (same bucket CloudFront uses as origin)
2. **CloudFront distribution** — serves `https://beespace.live`
3. **IAM role** — allows GitHub Actions to upload to that bucket and invalidate CloudFront

Note the **bucket name**, **AWS account ID**, **region**, and **CloudFront distribution ID** (e.g. `E1234ABCDEF`).

### B. Create an IAM role for GitHub OIDC

In **AWS IAM → Roles → Create role**:

1. **Trusted entity:** Web identity → **GitHub**
2. **Audience / conditions:** restrict to your repo, e.g.  
   `repo:VIDCOOL/BeeSpace.live:ref:refs/heads/main`  
   (Use GitHub’s “Configure GitHub OIDC” wizard or [AWS docs for GitHub OIDC](https://docs.github.com/en/actions/security-for-github-actions/security-hardening-your-deployments/configuring-openid-connect-in-amazon-web-services).)
3. **Permissions policy** (adjust bucket name and distribution ARN):

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": ["s3:ListBucket"],
      "Resource": "arn:aws:s3:::YOUR-BUCKET-NAME"
    },
    {
      "Effect": "Allow",
      "Action": ["s3:GetObject", "s3:PutObject", "s3:DeleteObject"],
      "Resource": "arn:aws:s3:::YOUR-BUCKET-NAME/*"
    },
    {
      "Effect": "Allow",
      "Action": "cloudfront:CreateInvalidation",
      "Resource": "arn:aws:cloudfront::YOUR-ACCOUNT-ID:distribution/YOUR-DISTRIBUTION-ID"
    }
  ]
}
```

4. Copy the role **ARN** (e.g. `arn:aws:iam::123456789012:role/github-beespace-live-publish`).

### C. Add GitHub repository secrets

In **GitHub → VIDCOOL/BeeSpace.live → Settings → Secrets and variables → Actions → Secrets**:

| Secret name | Value |
|-------------|--------|
| `AWS_ROLE_ARN` | IAM role ARN from step B |
| `AWS_S3_BUCKET` | Your S3 bucket name |
| `CLOUDFRONT_DISTRIBUTION_ID` | CloudFront distribution ID |

Optional **variable** (not secret): **Settings → Actions → Variables**

| Variable | Example |
|----------|---------|
| `AWS_REGION` | `us-west-1` or the region where your bucket lives |

### D. Enable GitHub Environments (optional)

The workflow uses `environment: production`. First run:

**Settings → Environments → New environment → `production`**

You can add required reviewers so merges do not publish until you approve (optional).

### E. Copy workflow into the BeeSpace repo

If these files are not in `BeeSpace.live` yet, copy from your Cursor/`cv` branch or add:

- `.github/workflows/deploy-beespace-live.yml`
- `docs/DEPLOY.md`

Commit to `main`, then test.

### F. Verify publish works

1. **Actions** tab → **Publish beespace.live** → **Run workflow** (workflow_dispatch), or push a tiny change to `main`.
2. Wait for green checkmark.
3. Hard-refresh https://beespace.live (or check a file you changed).

Until this succeeds, tell Grok Bot: **“Publish is not wired yet; PR merged but live site unchanged.”**

---

## Alternative: manual publish from your Mac

Use when Actions is not set up yet or for emergency fixes.

**Requirements:** [AWS CLI](https://aws.amazon.com/cli/) configured (`aws configure` or SSO) with permission to write the bucket.

From your site folder:

```bash
cd /Users/leorios/Desktop/BeeSpace

# Dry run — see what would upload
aws s3 sync . s3://YOUR-BUCKET-NAME/ --delete --dryrun \
  --exclude ".git/*" --exclude ".github/*" --exclude ".cursor/*" \
  --exclude ".DS_Store" --exclude "README.md" --exclude "GROKBOT.md"

# Upload
aws s3 sync . s3://YOUR-BUCKET-NAME/ --delete \
  --exclude ".git/*" --exclude ".github/*" --exclude ".cursor/*" \
  --exclude ".DS_Store" --exclude "README.md" --exclude "GROKBOT.md"

# Invalidate CloudFront (replace DISTRIBUTION_ID)
aws cloudfront create-invalidation \
  --distribution-id YOUR-DISTRIBUTION-ID \
  --paths "/*"
```

See [`scripts/publish-to-s3.sh.example`](../scripts/publish-to-s3.sh.example) for a template (copy locally; do not commit real bucket names if you prefer secrets-only).

---

## Fallback: access keys in GitHub Secrets (not recommended)

If OIDC is too much for now, you can use **repository secrets** `AWS_ACCESS_KEY_ID` and `AWS_SECRET_ACCESS_KEY` with a dedicated IAM user limited to the bucket + invalidation. Prefer OIDC for long-term use. Do **not** share keys with Grok Bot or put them in chat.

---

## Troubleshooting

| Symptom | Likely cause |
|---------|----------------|
| PR merged, site unchanged | Workflow missing, failed, or secrets not set |
| Actions fail on “AssumeRole” | OIDC trust policy wrong repo/org or role ARN typo |
| Upload OK, old content in browser | CloudFront cache — check invalidation step / distribution ID |
| 403 on S3 sync | IAM policy missing `s3:DeleteObject` for `--delete` |

---

## For Grok Bot / Cursor agents

- **May:** edit files, open PRs, merge when asked (if permitted).
- **May not:** configure AWS, create GitHub secrets, or accept AWS keys in conversation.
- **After merge:** if publish workflow exists and secrets are configured, say: “Merged; GitHub Actions should publish to beespace.live — check the Actions tab.”
- **If publish not configured:** say: “Merged to GitHub only; live site needs DEPLOY.md setup or manual S3 upload.”

See also [`GROKBOT.md`](../GROKBOT.md).
