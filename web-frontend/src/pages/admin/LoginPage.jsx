import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import '../../styles/admin.css'

function LoginPage() {
  const [user, setUser] = useState('')
  const [pass, setPass] = useState('')
  const [error, setError] = useState(false)
  const navigate = useNavigate()

  const handleLogin = (e) => {
    e.preventDefault()
    // kollar att fälten inte är tomma
    if (user && pass) {
      navigate('/admin/lunch')
    } else {
      setError(true)
    }
  }

  return (
    <div className="login-screen">
      <form className="login-card" onSubmit={handleLogin}>
        <div className="logo">Antons <span>Skafferi</span></div>
        <p className="subtitle">Administratörspanel</p>

        <div className="form-group">
          <label>Användarnamn</label>
          <input
            type="text"
            placeholder="admin"
            value={user}
            onChange={(e) => { setUser(e.target.value); setError(false) }}
          />
        </div>
        <div className="form-group">
          <label>Lösenord</label>
          <input
            type="password"
            placeholder="••••••••"
            value={pass}
            onChange={(e) => { setPass(e.target.value); setError(false) }}
          />
        </div>
        <button type="submit" className="btn-login">Logga in</button>
        {error && (
          <div className="login-error">Fyll i både användarnamn och lösenord.</div>
        )}
      </form>
    </div>
  )
}

export default LoginPage
