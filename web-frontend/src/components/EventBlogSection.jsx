import { useState, useEffect } from 'react'
import { getMusicEvents, getComments, addComment, deleteComment, likeComment, dislikeComment, likeEvent, dislikeEvent } from '../api/menuApi'

function parseDate(dateStr) {
  if (!dateStr) return { day: '?', month: '?' }
  const d = new Date(dateStr)
  const month = d.toLocaleString('sv-SE', { month: 'short' })
  return {
    day: d.getDate(),
    month: month.charAt(0).toUpperCase() + month.slice(1),
  }
}

function EventCard({ event }) {
  const [liked, setLiked] = useState(false)
  const [likes, setLikes] = useState(event.likes ?? 0)
  const [showComments, setShowComments] = useState(false)
  const [comments, setComments] = useState([])
  const [newComment, setNewComment] = useState('')
  const [commenterName, setCommenterName] = useState('')
  const [likedComments, setLikedComments] = useState({})

  useEffect(() => {
    getComments(event.id).then(setComments).catch(() => {})
  }, [event.id])

  const handleLike = () => {
    if (liked) {
      dislikeEvent(event.id)
      setLikes(l => l - 1)
    } else {
      likeEvent(event.id)
      setLikes(l => l + 1)
    }
    setLiked(!liked)
  }

  const handleComment = async () => {
    if (!newComment.trim()) return
    const name = commenterName.trim() || 'Gäst'
    const message = newComment.trim()
    const temp = { commentId: Date.now(), name, message, likes: 0 }
    setComments(prev => [...prev, temp])
    setNewComment('')
    try {
      await addComment(event.id, name, message)
    } catch {
      setComments(prev => prev.filter(c => c.commentId !== temp.commentId))
      return
    }
    try {
      const updated = await getComments(event.id)
      setComments(updated)
    } catch {}
  }

  const handleDeleteComment = async (commentId) => {
    try {
      await deleteComment(event.id, commentId)
      setComments(prev => prev.filter(c => c.commentId !== commentId))
    } catch {}
  }

  const handleCommentLike = (commentId) => {
    if (likedComments[commentId]) {
      dislikeComment(commentId)
      setComments(prev => prev.map(c => c.commentId === commentId ? { ...c, likes: c.likes - 1 } : c))
      setLikedComments(prev => ({ ...prev, [commentId]: false }))
    } else {
      likeComment(commentId)
      setComments(prev => prev.map(c => c.commentId === commentId ? { ...c, likes: c.likes + 1 } : c))
      setLikedComments(prev => ({ ...prev, [commentId]: true }))
    }
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
        <button className={`event-like-btn${liked ? ' liked' : ''}`} onClick={handleLike}>
          ♥ {likes}
        </button>
        <button className="event-comment-btn" onClick={() => setShowComments(!showComments)}>
          💬 {comments.length} {comments.length === 1 ? 'kommentar' : 'kommentarer'}
        </button>
      </div>

      {showComments && (
        <div className="event-comments">
          {comments.map(c => (
            <div className="event-comment" key={c.commentId}>
              <div className="comment-body">
                <div>
                  <div className="comment-author">{c.name}</div>
                  <div className="comment-text">{c.message}</div>
                  <div className="comment-actions">
                    <button className={`event-like-btn${likedComments[c.commentId] ? ' liked' : ''}`} onClick={() => handleCommentLike(c.commentId)}>
                      ♥ {c.likes}
                    </button>
                  </div>
                </div>
                <button className="comment-delete-btn" onClick={() => handleDeleteComment(c.commentId)}>✕</button>
              </div>
            </div>
          ))}
          <div className="comment-input-row">
            <input
              type="text"
              placeholder="Ditt namn (valfritt)"
              value={commenterName}
              onChange={(e) => setCommenterName(e.target.value)}
            />
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
          <div className="event-column">
            <p className="event-column-label">Tidigare event</p>
            {previous.map(event => (
              <EventCard key={event.id} event={event} />
            ))}
          </div>

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
