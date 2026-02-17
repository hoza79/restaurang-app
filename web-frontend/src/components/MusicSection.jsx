import { artists } from '../data/mockData'

function MusicSection() {
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
          {artists.map((artist) => (
            <div className="music-item" key={artist.id}>
              <div className="date-block">
                <div className="day">{artist.date.day}</div>
                <div className="month">{artist.date.month}</div>
                <div className="weekday">{artist.date.weekday}</div>
              </div>
              <div>
                <h3>{artist.name}</h3>
                <p className="genre">{artist.genre}</p>
                <p className="desc">{artist.description}</p>
              </div>
              <span className="time-label">{artist.time}</span>
            </div>
          ))}
        </div>
      </div>
    </section>
  )
}

export default MusicSection
