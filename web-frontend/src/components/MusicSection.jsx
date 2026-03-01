import { useState, useEffect } from 'react'
import { getMusicEvents } from '../api/menuApi'

function formatMusicDate(dateStr) {
  if (!dateStr) return { day: '?', month: '?', weekday: '?', time: '' }
  const d = new Date(dateStr)
  const day = d.getDate()
  const month = d.toLocaleString('sv-SE', { month: 'long' })
  const weekday = d.toLocaleString('sv-SE', { weekday: 'long' })
  const time = `Kl. ${d.getHours().toString().padStart(2, '0')}:${d.getMinutes().toString().padStart(2, '0')}`
  return {
    day,
    month: month.charAt(0).toUpperCase() + month.slice(1),
    weekday: weekday.charAt(0).toUpperCase() + weekday.slice(1),
    time,
  }
}

function MusicSection() {
  const [events, setEvents] = useState([])

  useEffect(() => {
    getMusicEvents().then(setEvents)
  }, [])

  return (
    <section className="music" id="musik">
      <div className="container">
        <p className="section-label">Livemusik</p>
        <h2 className="section-title">Veckans <em>Artister</em></h2>
        <div className="gold-line"></div>
        <p className="section-intro">
          Njut av livemusik till middagen. Vi rekommenderar bordsbokning på musikkvällar.
        </p>

        <div className="music-list">
          {events.map((event) => {
            const { day, month, weekday, time } = formatMusicDate(event.date)
            return (
              <div className="music-item" key={event.id}>
                <div className="date-block">
                  <div className="day">{day}</div>
                  <div className="month">{month}</div>
                  <div className="weekday">{weekday}</div>
                </div>
                <div>
                  <h3>{event.title}</h3>
                  <p className="desc">{event.description}</p>
                </div>
                {time && <span className="time-label">{time}</span>}
              </div>
            )
          })}
        </div>
      </div>
    </section>
  )
}

export default MusicSection
