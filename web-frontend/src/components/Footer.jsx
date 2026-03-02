function Footer() {
  return (
    <footer className="site-footer">
      <div className="footer-content">
        <div className="footer-brand">
          <h3>Antons Skafferi</h3>
          <p>
            Döpt efter Anton Franzén och hans kärlek till genuin svensk matkultur.
            Vi lagar ärlig husmanskost med de bästa lokala och säsongsbetonade råvarorna.
          </p>
        </div>
        <div>
          <h4>Öppettider</h4>
          <ul>
            <li>Mån-Fre: 11-14, 17-22</li>
            <li>Lördag: 17-22</li>
            <li>Söndag: Stängt</li>
          </ul>
        </div>
        <div>
          <h4>Hitta oss</h4>
          <ul>
            <li>Storgatan 12</li>
            <li>Stockholm</li>
            <li>08-123 456 78</li>
            <li>info@antonsskafferi.se</li>
          </ul>
        </div>
      </div>
      <div className="footer-bottom">
        &copy; 2026 Antons Skafferi
      </div>
    </footer>
  )
}

export default Footer
