import { useState, useEffect } from 'react'
import { getLunchMenu } from '../api/menuApi'

function LunchSection() {
  const [lunchData, setLunchData] = useState(null)
  const [error, setError] = useState(false)
  const [activeDay, setActiveDay] = useState(null)

  useEffect(() => {
    getLunchMenu()
      .then(data => {
        setLunchData(data)
        // välj dagens veckodag som default
        const today = new Date().getDay()
        const todayMealDay = today >= 1 && today <= 5 ? today : null
        const availableDays = data.days.map(d => d.dayNum)
        if (todayMealDay && availableDays.includes(todayMealDay)) {
          setActiveDay(todayMealDay)
        } else if (availableDays.length > 0) {
          setActiveDay(availableDays[0])
        }
      })
      .catch(() => setError(true))
  }, [])

  if (error) return <section className="lunch" id="lunch"><div className="container"><p>Kunde inte ladda lunchmenyn.</p></div></section>
  if (!lunchData) return <section className="lunch" id="lunch"><div className="container"><p>Laddar meny...</p></div></section>

  const currentDay = lunchData.days.find(d => d.dayNum === activeDay)

  return (
    <section className="lunch" id="lunch">
      <div className="container">
        <p className="section-label">Vardagar 11:00 - 14:00</p>
        <h2 className="section-title">Dagens <em>Lunch</em></h2>
        <div className="gold-line"></div>
        <p className="section-intro">
          Hemlagad husmanskost, varje dag en ny upplevelse. Lagad med omsorg av färska, lokala råvaror.
        </p>

        <div className="day-tabs">
          {lunchData.days.map(day => (
            <button
              key={day.dayNum}
              className={`day-tab${day.dayNum === activeDay ? ' active' : ''}`}
              onClick={() => setActiveDay(day.dayNum)}
            >
              {day.label}
            </button>
          ))}
        </div>

        {currentDay && (
          <div className="lunch-grid">
            {currentDay.items.map((item, i) => (
              <div className="lunch-item" key={item.id}>
                <div className="lunch-item-number">{String(i + 1).padStart(2, '0')}</div>
                <h3>{item.name}</h3>
                <p>{item.description}</p>
                <span className="price">{item.price} kr</span>
              </div>
            ))}
            <div className="lunch-includes">
              {lunchData.includes}
            </div>
          </div>
        )}

        {lunchData.source === 'mock' && (
          <p style={{ textAlign: 'center', fontSize: '0.75rem', color: '#c9a84c', opacity: 0.6, marginTop: '2rem' }}>
            Visar mockdata - backend ej ansluten
          </p>
        )}
      </div>
    </section>
  )
}

export default LunchSection
