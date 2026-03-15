import { useState, useEffect } from 'react'
import { getMusicEvents } from '../api/menuApi'

function parseDate(dateStr) {
  if (!dateStr) return { day: '?', month: '?' }
  const d = new Date(dateStr)
  const month = d.toLocaleString('sv-SE', { month: 'short' })
  return {
    day: d.getDate(),
    month: month.charAt(0).toUpperCase() + month.slice(1),
  }
}

// ett event-kort med likes och kommentarer
function EventCard({ event }) {
  const [liked, setLiked] = useState(false)
  const [likes, setLikes] = useState(0)
  const [showComments, setShowComments] = useState(false)
  const [comments, setComments] = useState([])
  const [newComment, setNewComment] = useState('')

  const handleLike = () => {
    setLikes(liked ? likes - 1 : likes + 1)
    setLiked(!liked)
  }

  const handleComment = () => {
    if (!newComment.trim()) return
    setComments([...comments, { id: Date.now(), author: 'Du', text: newComment }])
    setNewComment('')
  }

  const { day, month } = parseDate(event.date)

  return (
    <div className="event-card">
      <div className="event-card-top">
        {event.imgPath ? (
          <img src={`/api/music/images/${event.imgPath}`} alt={event.title} className="event-date-block" style={{ objectFit: 'cover', padding: 0, width: '70px', height: '70px' }} />
        ) : (
          <div className="event-date-block">
            <div className="event-day">{day}</div>
            <div className="event-month">{month}</div>
          </div>
        )}
        <div className="event-info">
          <h3>{event.title}</h3>
          <p className="event-desc">{event.description}</p>
        </div>
      </div>

      <div className="event-actions">
        {/* like-knapp */}
        <button className={`event-like-btn${liked ? ' liked' : ''}`} onClick={handleLike}>
          ♥ {likes}
        </button>
        {/* visa/göm kommentarer */}
        <button className="event-comment-btn" onClick={() => setShowComments(!showComments)}>
          💬 {comments.length} {comments.length === 1 ? 'kommentar' : 'kommentarer'}
        </button>
      </div>

      {showComments && (
        <div className="event-comments">
          {comments.map(c => (
            <div className="event-comment" key={c.id}>
              <span className="comment-author">{c.author}</span>
              <span className="comment-text">{c.text}</span>
            </div>
          ))}
          <div className="comment-input-row">
            <input
              type="text"
              placeholder="Skriv en kommentar..."
              value={newComment}
              onChange={(e) => setNewComment(e.target.value)}
              onKeyDown={(e) => e.key === 'Enter' && handleComment()}
            />
            <button onClick={handleComment}>Skicka</button>
          </div>
        </div>
      )}
    </div>
  )
}

function EventBlogSection() {
  const [previous, setPrevious] = useState([])
  const [upcoming, setUpcoming] = useState([])

  useEffect(() => {
    getMusicEvents().then(events => {
      const now = new Date()
      const sorted = [...events].sort((a, b) => new Date(a.date) - new Date(b.date))
      const prev = sorted.filter(e => e.date && new Date(e.date) < now).slice(-3)
      const next = sorted.filter(e => e.date && new Date(e.date) >= now).slice(0, 3)
      setPrevious(prev)
      setUpcoming(next)
    })
  }, [])

  return (
    <section className="event-blog" id="events">
      <div className="container">
        <p className="section-label">Livemusik</p>
        <h2 className="section-title">Tidigare & <em>Kommande</em></h2>
        <div className="gold-line"></div>
        <p className="section-intro">
          Följ med på kvällarna - se vad som hänt och vad som är på gång. Dela dina upplevelser med oss.
        </p>

        <div className="event-blog-columns">
          {/* tidigare events */}
          <div className="event-column">
            <p className="event-column-label">Tidigare event</p>
            {previous.map(event => (
              <EventCard key={event.id} event={event} />
            ))}
          </div>

          {/* kommande events */}
          <div className="event-column">
            <p className="event-column-label">Kommande event</p>
            {upcoming.map(event => (
              <EventCard key={event.id} event={event} />
            ))}
          </div>
        </div>
      </div>
    </section>
  )
}

export default EventBlogSection
