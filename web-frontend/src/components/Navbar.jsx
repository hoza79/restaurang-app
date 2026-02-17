import '../styles/public.css'

function Navbar() {
  return (
    <nav className="public-nav">
      <a href="#" className="logo">Antons <span>Skafferi</span></a>
      <ul>
        <li><a href="#lunch">Lunch</a></li>
        <li><a href="#carte">À la carte</a></li>
        <li><a href="#musik">Musik</a></li>
        <li><a href="#boka">Boka bord</a></li>
      </ul>
    </nav>
  )
}

export default Navbar
