import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.tsx'
import { OpenAPI } from './api'

// Configure OpenAPI to use token from localStorage
OpenAPI.TOKEN = () => Promise.resolve(localStorage.getItem('token') || '')

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <App />
  </StrictMode>,
)
