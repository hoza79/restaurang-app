import { useState } from 'react'
import { NavLink, Outlet, useNavigate } from 'react-router-dom'
import '../../styles/admin.css'

function AdminLayout() {
  const [sidebarOpen, setSidebarOpen] = useState(false)
  const navigate = useNavigate()

  const navItems = [
    { to: '/admin/lunch', icon: '\u{1F374}', label: 'Lunchmeny' },
    { to: '/admin/carte', icon: '\u{1F37D}', label: 'À la carte' },
    { to: '/admin/musik', icon: '\u{1F3B5}', label: 'Musik' },
    { to: '/admin/bokningar', icon: '\u{1F4C5}', label: 'Bokningar' },
  ]

  const handleLogout = (e) => {
    e.preventDefault()
    navigate('/admin')
  }

  return (
    <>
      <button className="mobile-toggle" onClick={() => setSidebarOpen(!sidebarOpen)}>
        &#9776;
      </button>
      <div
        className={`sidebar-overlay${sidebarOpen ? ' open' : ''}`}
        onClick={() => setSidebarOpen(false)}
      />

      <div className="dashboard">
        <aside className={`sidebar${sidebarOpen ? ' open' : ''}`}>
          <div className="sidebar-logo">
            <h2>Antons <span>Skafferi</span></h2>
            <p>Admin</p>
          </div>

          <ul className="sidebar-nav">
            {navItems.map((item) => (
              <li key={item.to}>
                <NavLink
                  to={item.to}
                  className={({ isActive }) => isActive ? 'active' : ''}
                  onClick={() => setSidebarOpen(false)}
                >
                  <span className="icon">{item.icon}</span> {item.label}
                </NavLink>
              </li>
            ))}
          </ul>

          <div className="sidebar-bottom">
            <a href="/">&#8592; Tillbaka till hemsidan</a>
            <a href="#" onClick={handleLogout}>Logga ut</a>
          </div>
        </aside>

        <main className="admin-main">
          <Outlet />
        </main>
      </div>
    </>
  )
}

export default AdminLayout
