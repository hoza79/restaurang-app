import { carteMenu } from '../data/mockData'

function CarteSection() {
  return (
    <section className="carte" id="carte">
      <div className="container">
        <p className="section-label">Kvällens meny - Från kl. 17:00</p>
        <h2 className="section-title">À la <em>Carte</em></h2>
        <div className="gold-line"></div>
        <p className="section-intro">
          Vår kvällsmeny formas av säsongen. Lokala jägare och fiskare förser oss med det allra bästa.
        </p>

        <div className="carte-columns">
          {carteMenu.map((cat) => (
            <div className="carte-category" key={cat.category}>
              <h3>{cat.category}</h3>
              {cat.items.map((item) => (
                <div className="carte-item" key={item.id}>
                  <div className="carte-item-top">
                    <h4>{item.name}</h4>
                    <span className="dots"></span>
                    <span className="price">{item.price} kr</span>
                  </div>
                  <p>{item.description}</p>
                </div>
              ))}
            </div>
          ))}

          <div className="carte-note">
            "Vår meny lever med årstiderna. Varje vecka tar vi emot det bästa från lokala jägare
            och fiskare - det som naturen ger, det serverar vi."
          </div>
        </div>
      </div>
    </section>
  )
}

export default CarteSection
