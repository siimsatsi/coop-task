<template>
  <div class="shell">
    <header class="site-header">
      <div class="header-inner">
        <span class="wordmark">COOP <em>Laen</em></span>
        <nav>
          <RouterLink to="/">Application</RouterLink>
          <RouterLink to="/review">Review</RouterLink>
        </nav>
      </div>
      <div class="header-rule" />
    </header>
    <main class="page-content">
      <RouterView />
    </main>
  </div>
</template>

<style scoped>
.shell { min-height: 100vh; }

.site-header { padding: 1.5rem 3rem 0; }

.header-inner {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  padding-bottom: 1rem;
}

.wordmark {
  font-family: var(--font-display);
  font-size: 1.1rem;
  letter-spacing: 0.12em;
}
.wordmark em { font-style: italic; color: var(--accent); }

nav { display: flex; gap: 2rem; }
nav a {
  font-size: 12px;
  letter-spacing: 0.1em;
  text-transform: uppercase;
  color: var(--muted);
  text-decoration: none;
  transition: color 0.15s;
}
nav a:hover, nav a.router-link-active { color: var(--ink); }

.header-rule { border-top: 1px solid var(--ink); }

.page-content { padding: 3rem; max-width: 860px; }
</style>
