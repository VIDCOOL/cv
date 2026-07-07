# cv

A zero-dependency static HTML personal CV website (`index.html`, `contact.html`, `hobbies.html`, `dog.png`). No build step, no package manager, no backend, no database.

## Cursor Cloud specific instructions

- This is a static site. There are no dependencies to install and nothing to build; the update script is a no-op.
- Serve it for local viewing with `python3 -m http.server 8000` from the repo root, then open `http://localhost:8000/index.html`. Opening the files via `file://` also works, but serving over HTTP is closer to real usage and ensures relative links/image load correctly.
- There is no lint/test/build tooling in this repo. "Testing" means opening the pages in a browser and checking they render and links navigate.
- The contact form (`contact.html`) uses `action="mailto:..."`, so submitting it hands off to the local mail client rather than any server; there is no backend to run.
