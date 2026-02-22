import { useState } from 'react'
import { eventBlog } from '../data/mockData'

// ett event-kort med likes och kommentarer
function EventCard({ event }) {
  const [liked, setLiked] = useState(false)
  const [likes, setLikes] = useState(event.likes)
  const [showComments, setShowComments] = useState(false)
  const [comments, setComments] = useState(event.comments)
  const [newComment, setNewComment] = useState('')

  const handleLike = () => {
    if (liked) {
      setLikes(likes - 1)
    } else {
      setLikes(likes + 1)
    }
    setLiked(!liked)
  }

  const handleComment = () => {
    if (!newComment.trim()) return
    setComments([...comments, { id: Date.now(), author: 'Du', text: newComment }])
    setNewComment('')
  }

  return (
    <div className="event-card">
      <div className="event-card-top">
        <div className="event-date-block">
          <div className="event-day">{event.date.day}</div>
          <div className="event-month">{event.date.month}</div>
        </div>
        <div className="event-info">
          <h3>{event.artist}</h3>
          <p className="event-genre">{event.genre}</p>
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
            {eventBlog.previous.map(event => (
              <EventCard key={event.id} event={event} />
            ))}
          </div>

          {/* kommande events */}
          <div className="event-column">
            <p className="event-column-label">Kommande event</p>
            {eventBlog.upcoming.map(event => (
              <EventCard key={event.id} event={event} />
            ))}
          </div>
        </div>
      </div>
    </section>
  )
}

export default EventBlogSection
