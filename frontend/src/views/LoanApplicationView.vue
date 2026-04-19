<template>
  <div>
    <div class="page-title">
      <p class="muted mono">01</p>
      <h1>Laenutaotlus</h1>
      <p class="subtitle muted">Submit a new loan application</p>
    </div>

    <hr class="rule" />

    <div v-if="result" class="result-block" :class="result.status.toLowerCase()">
      <p class="mono result-status">{{ result.status }}</p>
      <p v-if="result.rejectionReason" class="muted">Reason: {{ result.rejectionReason }}</p>
      <p v-if="result.id" class="muted">
        Application ID: <span class="mono">{{ result.id }}</span>
      </p>
      <div style="margin-top: 1rem; display: flex; gap: 1rem;">
        <button @click="result = null">New application</button>
        <button v-if="result.status === 'IN_REVIEW'" class="primary"
                @click="$router.push('/review')">Review →</button>
      </div>
    </div>

    <form v-else @submit.prevent="submit" class="loan-form">
      <div class="form-row">
        <div class="field">
          <label>First name</label>
          <input v-model="form.firstName" maxlength="32" required placeholder="Mari" />
        </div>
        <div class="field">
          <label>Last name</label>
          <input v-model="form.lastName" maxlength="32" required placeholder="Tamm" />
        </div>
      </div>

      <div class="field">
        <label>Personal code <span class="muted">(isikukood)</span></label>
        <input v-model="form.personalCode" class="mono" maxlength="11"
               pattern="\d{11}" required placeholder="38001085718" />
      </div>

      <div class="form-row three">
        <div class="field">
          <label>Loan amount <span class="muted">min €5,000</span></label>
          <input v-model.number="form.loanAmount" class="mono" type="number"
                 min="5000" step="100" required placeholder="25000" />
        </div>
        <div class="field">
          <label>Term <span class="muted">months (6–360)</span></label>
          <input v-model.number="form.termMonths" class="mono" type="number"
                 min="6" max="360" required placeholder="60" />
        </div>
      </div>

      <div class="form-row">
        <div class="field">
          <label>Interest margin <span class="muted">%</span></label>
          <input v-model.number="form.interestMargin" class="mono" type="number"
                 min="0" step="0.001" required placeholder="1.500" />
        </div>
        <div class="field">
          <label>Base rate (Euribor 6m) <span class="muted">%</span></label>
          <input v-model.number="form.baseInterestRate" class="mono" type="number"
                 min="0" step="0.001" required placeholder="3.840" />
        </div>
      </div>

      <p v-if="error" class="error-msg">{{ error }}</p>

      <div style="margin-top: 2.5rem;">
        <button type="submit" class="primary" :disabled="loading">
          {{ loading ? 'Submitting...' : 'Submit application' }}
        </button>
      </div>
    </form>
  </div>
</template>

<script setup lang="ts">
import {onMounted, ref} from 'vue'

interface LoanResult {
  id: string
  firstName: string
  lastName: string
  personalCode: string
  loanAmount: number
  termMonths: number
  interestMargin: number
  baseInterestRate: number
  status: string
  rejectionReason: string | null
}

interface FormData {
  firstName: string
  lastName: string
  personalCode: string
  loanAmount: number | null
  termMonths: number | null
  interestMargin: number | null
  baseInterestRate: number | null
}

const API = 'http://localhost:8080/api/loans'

const form = ref<FormData>({
  firstName: '', lastName: '', personalCode: '',
  loanAmount: null, termMonths: null,
  interestMargin: null, baseInterestRate: null
})

onMounted(async () => {
  const res = await fetch('http://localhost:8080/api/parameters')
  if (res.ok) {
    const params = await res.json()
    const euribor = params.find((p: { key: string }) => p.key === 'euribor_6m')
    if (euribor) form.value.baseInterestRate = euribor.value
  }
})

const loading = ref(false)
const error = ref('')
const result = ref<LoanResult | null>(null)

async function submit() {
  loading.value = true
  error.value = ''
  try {
    const res = await fetch(API, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(form.value)
    })
    if (!res.ok) {
      const err = await res.json()
      throw new Error(err.detail || 'Submission failed')
    }
    result.value = await res.json()
  } catch (e: unknown) {
    error.value = e instanceof Error ? e.message : 'Submission failed'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.page-title { margin-bottom: 1.5rem; }
.page-title h1 { font-size: 2.4rem; margin: 0.25rem 0 0.5rem; }
.subtitle { font-size: 14px; }

.loan-form { display: flex; flex-direction: column; gap: 1.8rem; max-width: 560px; }

.form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 2rem; }
.form-row.three { grid-template-columns: 1fr 1fr; }

.field { display: flex; flex-direction: column; gap: 4px; }
label { font-size: 11px; letter-spacing: 0.1em; text-transform: uppercase; color: var(--muted); }

.result-block {
  padding: 1.5rem;
  border-left: 3px solid var(--rule);
  margin-bottom: 2rem;
}
.result-block.approved { border-color: var(--success); }
.result-block.rejected { border-color: var(--danger); }
.result-block.in_review { border-color: var(--accent); }

.result-status {
  font-size: 1.1rem;
  font-weight: 500;
  letter-spacing: 0.08em;
  margin-bottom: 0.5rem;
}
</style>
