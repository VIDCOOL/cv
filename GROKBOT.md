# Grok Bot — BeeSpace.live site instructions

Use this document when you maintain **https://beespace.live** via GitHub.

## Repository and live site

| Item | Value |
|------|--------|
| **GitHub repo** | `VIDCOOL/BeeSpace.live` |
| **Default branch** | `main` |
| **Live URL** | https://beespace.live |
| **Hosting** | Static files on **Amazon S3**, served through **CloudFront** |
| **Local path (owner)** | `/Users/leorios/Desktop/BeeSpace/` |

Changes merged to `main` update the public site **only after deploy** (GitHub Actions or manual S3 sync). If the site did not change after your PR merged, say so and check whether deploy ran.

---

## Your role

You help update the **BeeSpace** marketing and education site for a **vacant-lot pollinator habitat pilot** (property owners, volunteers, neighbors). Priorities:

1. Accurate, calm, safety-conscious copy
2. Small, focused diffs — do not refactor unrelated files
3. **Branch → pull request → merge to `main`** (do not force-push `main`)
4. Never commit secrets, API keys, or personal tokens

---

## GitHub workflow (required)

1. Fetch latest `main`.
2. Create a descriptive branch, e.g. `grok/fix-apply-form` or `grok/update-hero-copy`.
3. Make edits; commit with clear messages.
4. Open a **pull request** against `main` with a short summary and test notes.
5. Do **not** merge unless the user explicitly asks you to merge.

If the GitHub plugin asks for approval, prefer opening PRs over direct pushes to `main` when branch protection is enabled.

---

## Site structure (typical paths)

```
index.html              # Main landing page (long single-page layout)
css/styles.css          # Primary design system — forest / editorial theme
js/main.js              # Nav, video modal, almanac modal, contact form subject
apply/index.html        # Residential host application
apply/apply.js          # Conditional form fields
apply/thanks/           # Post-submit thank-you page
guides/                 # Education pages (some use inline styles)
videos/                 # YouTube embed page
assets/                 # Images, logos, icons
docs/                   # PDFs (overview, RFP)
```

**Subpages** (`/guides/`, `/videos/`) should eventually match the main site header/footer and `css/styles.css`. When touching those pages, align typography and colors with the homepage unless the task is copy-only.

---

## Audiences and tone

Write for three groups:

- **Property owners** — voluntary pilot, plain-language agreements, visible care of lots
- **Volunteers** — orientation, safety, predictable shifts, no expertise required on day one
- **Neighbors / public** — education, safety FAQ, trustworthy guidance

Tone: professional, warm, factual. Avoid hype. Be explicit about pilot limits (site suitability, permits, voluntary participation).

**Geography:** Prefer naming the pilot region when relevant (e.g. Long Beach, Los Angeles County, Orange County, Southern California). Avoid repeating vague “local communities” without geographic context.

---

## Forms and integrations

| Form | Location | Backend |
|------|----------|---------|
| General contact | `index.html` `#contact` | Formspree — action in form markup |
| Host application | `apply/index.html` | Formspree — must use a **real** form ID (not `REPLACE_WITH_APPLICATION_FORM_ID`) |

- Do not change Formspree endpoints unless the user provides a new ID.
- Contact form uses a honeypot field `_honey` — preserve it.
- Success redirect: contact uses `?thanks=1` on homepage; apply uses `/apply/thanks/`.

---

## Embedded services (do not break)

- **Intro video:** YouTube embed in modal (`js/main.js`)
- **Weekly Almanac:** iframe to Cloud Run app (modal + external link in `#almanac`)
- **Guide pages:** may embed external extension content in iframes — preserve URLs unless updating intentionally

---

## Design and accessibility

- Primary stylesheet: `css/styles.css` (CSS variables: `--forest`, `--paper`, `--ink`, etc.)
- Fonts: **Fraunces** (display), **DM Sans** (body) via Google Fonts
- Keep skip link, landmarks, and modal `aria-*` attributes when editing HTML
- **Link contrast:** body links must meet WCAG AA (~4.5:1). Avoid light sage link color on warm paper background; prefer darker green for default links

---

## Performance

Large images under `assets/` affect load time. When adding or replacing images:

- Prefer compressed WebP or optimized JPEG/PNG
- Use appropriate dimensions (not multi‑MB hero files)
- Use `loading="lazy"` for below-the-fold images; `loading="eager"` only for critical hero content

---

## Known issues (good first tasks)

1. **`/apply/` Formspree placeholder** — replace `REPLACE_WITH_APPLICATION_FORM_ID` with the live form ID when the user provides it
2. **Missing SEO files** — add `robots.txt` and `sitemap.xml` at repo root if requested
3. **Guide/video pages** — inconsistent styling vs homepage
4. **Partner logos** — some load from third-party CDNs; prefer local copies in `assets/` when updating

---

## Secrets and config

| Store here | Do not commit |
|------------|----------------|
| GitHub Actions secrets | AWS credentials, bucket name, CloudFront distribution ID |
| Grok Bot Secrets (if needed) | Formspree keys only if user asks you to store them there |
| Repo | Public HTML/CSS/JS only |

Never paste AWS keys, Formspree secrets, or deploy credentials into chat logs or committed files.

---

## Deploy (after merge)

If **GitHub Actions** deploy workflow exists (`.github/workflows/deploy.yml`):

- Merging to `main` should sync to S3 and invalidate CloudFront
- After merge, confirm the Actions tab shows a successful run before telling the user the site is live

If **no workflow** exists, tell the user: *“Changes are on GitHub; live site needs manual S3 upload or deploy setup.”*

---

## What not to do

- Do not rename the repo or change GitHub org settings without explicit instruction
- Do not delete PDFs in `docs/` without confirmation
- Do not remove partner affiliation blocks or safety FAQ without confirmation
- Do not sell honey or products on behalf of BeeSpace (volunteer advisories clarify personal sharing only)
- Do not commit `.DS_Store`, `.env`, or local editor junk — respect `.gitignore`

---

## Example tasks (how to respond)

**User:** “Update the hero to mention Long Beach.”  
→ Edit `index.html` hero/subbar copy only; open PR; note line changed.

**User:** “Fix the broken host application.”  
→ Check `apply/index.html` and meta `beespace-formspree-application`; fix Formspree ID if provided; verify `apply.js` still toggles conditional fields.

**User:** “Add a new guide link on the homepage.”  
→ Add one card in `#resources` resource grid; link to existing `/guides/...` path; match existing card pattern.

---

## Handoff to Cursor (optional)

For large refactors, visual QA, or Cloud Agent testing, the user may hand off to **Cursor Cloud Agents** on the same repo. Your PR branch should be pushed to GitHub so Cursor can check out and continue work.

---

## Contact reference (public on site)

- **Leo Rios — BeeSpace**
- Email shown on site: `leo.rios@protonmail.com`
- Site: https://beespace.live

---

*Last updated for Grok Bot + GitHub plugin workflow. Attach this file to your BeeSpace Bot or place it in the repo root so agents can read it.*
