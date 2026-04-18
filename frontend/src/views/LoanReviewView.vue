<template>
  <div>
    <div class="page-title">
      <p class="muted mono">02</p>
      <h1>Loan Review</h1>
      <p class="subtitle muted">Review and action a pending application</p>
    </div>

    <hr class="rule" />

    <div class="lookup">
      <div class="field" style="max-width: 400px;">
        <label>Select application</label>
        <select v-model="loanId" @change="fetchLoan">
          <option value="">— choose an application —</option>
          <option v-for="l in applications" :key="l.id" :value="l.id">
            {{ l.firstName }} {{ l.lastName }} — {{ l.personalCode }} — €{{ l.loanAmount.toLocaleString() }}
          </option>
        </select>
      </div>
    </div>

    <p v-if="error" class="error-msg" style="margin-top: 1rem;">{{ error }}</p>

    <template v-if="loan">
      <hr class="rule" />

      <div class="applicant-grid">
        <div class="info-block">
          <p class="label">Applicant</p>
          <p class="value">{{ loan.firstName }} {{ loan.lastName }}</p>
        </div>
        <div class="info-block">
          <p class="label">Personal code</p>
          <p class="value mono">{{ loan.personalCode }}</p>
        </div>
        <div class="info-block">
          <p class="label">Status</p>
          <p class="value mono" :class="'status-' + loan.status.toLowerCase()">{{ loan.status }}</p>
        </div>
        <div class="info-block">
          <p class="label">Loan amount</p>
          <p class="value mono">€{{ loan.loanAmount.toLocaleString() }}</p>
        </div>
        <div class="info-block">
          <p class="label">Term</p>
          <p class="value mono">{{ loan.termMonths }} months</p>
        </div>
        <div class="info-block">
          <p class="label">Total rate</p>
          <p class="value mono">{{ (loan.interestMargin + loan.baseInterestRate).toFixed(3) }}%</p>
        </div>
      </div>

      <template v-if="loan.status === 'IN_REVIEW'">
        <hr class="rule" />
        <div class="actions">
          <button class="success" @click="approve" :disabled="acting">Approve</button>
          <div class="reject-group">
            <button class="danger" @click="reject" :disabled="acting || !rejectReason">Reject</button>
          </div>
        </div>
      </template>

      <template v-if="loan.paymentSchedule?.length">
        <hr class="rule" />
        <h2 style="font-size: 1.1rem; margin-bottom: 1rem;">Payment schedule</h2>
        <div class="schedule-wrap">
          <table class="schedule">
            <thead>
            <tr>
              <th>#</th><th>Date</th><th>Principal</th>
              <th>Interest</th><th>Total</th><th>Balance</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="e in loan.paymentSchedule" :key="e.id">
              <td class="mono">{{ e.paymentNumber }}</td>
              <td class="mono">{{ e.paymentDate }}</td>
              <td class="mono">{{ e.principal.toFixed(2) }}</td>
              <td class="mono">{{ e.interest.toFixed(2) }}</td>
              <td class="mono accent">{{ e.totalPayment.toFixed(2) }}</td>
              <td class="mono muted">{{ e.remainingBalance.toFixed(2) }}</td>
            </tr>
            </tbody>
          </table>
        </div>
      </template>
    </template>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'

interface PaymentEntry {
  id: string
  paymentNumber: number
  paymentDate: string
  principal: number
  interest: number
  totalPayment: number
  remainingBalance: number
}

interface LoanSummary {
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
  paymentSchedule: PaymentEntry[]
}

const API = 'http://localhost:8080/api/loans'
const loanId = ref('')
const loan = ref<LoanSummary | null>(null)
const loading = ref(false)
const acting = ref(false)
const error = ref('')
const rejectReason = ref('')
const applications = ref<LoanSummary[]>([])

async function fetchLoan() {
  if (!loanId.value) return
  loading.value = true
  error.value = ''
  try {
    const res = await fetch(`${API}/${loanId.value}`)
    if (!res.ok) throw new Error('Loan not found')
    loan.value = await res.json()
  } catch (e: unknown) {
    error.value = e instanceof Error ? e.message : 'Unknown error'
  } finally {
    loading.value = false
  }
}

async function fetchApplications() {
  const res = await fetch(API)
  if (res.ok) applications.value = await res.json()
}

onMounted(fetchApplications)

async function approve() {
  acting.value = true
  try {
    const res = await fetch(`${API}/${loanId.value}/approve`, { method: 'POST' })
    loan.value = await res.json()
    await fetchApplications()
  } finally {
    acting.value = false
  }
}

async function reject() {
  acting.value = true
  try {
    const res = await fetch(`${API}/${loanId.value}/reject`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ reason: rejectReason.value })
    })
    loan.value = await res.json()
    await fetchApplications()
  } finally {
    acting.value = false
  }
}
</script>

<style scoped>
.page-title { margin-bottom: 1.5rem; }
.page-title h1 { font-size: 2.4rem; margin: 0.25rem 0 0.5rem; }

.lookup { display: flex; align-items: flex-end; gap: 1.5rem; margin-bottom: 0.5rem; }
.field { display: flex; flex-direction: column; gap: 4px; flex: 1; }
label { font-size: 11px; letter-spacing: 0.1em; text-transform: uppercase; color: var(--muted); }

.applicant-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1.5rem 2rem;
}
.info-block .label {
  font-size: 11px; letter-spacing: 0.1em;
  text-transform: uppercase; color: var(--muted); margin-bottom: 4px;
}
.info-block .value { font-size: 1rem; }
.status-approved { color: var(--success); }
.status-rejected { color: var(--danger); }
.status-in_review { color: var(--accent); }

.actions { display: flex; align-items: center; gap: 1.5rem; flex-wrap: wrap; }
.reject-group { display: flex; align-items: flex-end; gap: 1rem; }
.reject-group select { width: 200px; }

.schedule-wrap { overflow-x: auto; }
.schedule { width: 100%; border-collapse: collapse; font-size: 13px; }
.schedule th {
  font-size: 10px; letter-spacing: 0.1em; text-transform: uppercase;
  color: var(--muted); text-align: right; padding: 6px 12px 6px 0;
  border-bottom: 1px solid var(--ink);
}
.schedule th:first-child { text-align: left; }
.schedule td {
  text-align: right; padding: 5px 12px 5px 0;
  border-bottom: 1px solid var(--rule);
}
.schedule td:first-child { text-align: left; }
.schedule tr:hover td { background: var(--accent-light); }
.accent { color: var(--accent); }
</style>
