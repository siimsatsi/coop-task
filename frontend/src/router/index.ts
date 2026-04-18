import { createRouter, createWebHistory } from 'vue-router'
import LoanApplicationView from '../views/LoanApplicationView.vue'
import LoanReviewView from '../views/LoanReviewView.vue'

export default createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', component: LoanApplicationView },
    { path: '/review', component: LoanReviewView },
  ]
})
