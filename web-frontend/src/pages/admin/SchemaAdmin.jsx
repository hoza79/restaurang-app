import { useState, useEffect, useRef } from 'react'
import { getEmployees, getShiftsForWeek, addShift, updateShift, deleteShift } from '../../api/menuApi'

const SHIFT_TYPES = {
  DAG: { label: 'Dagspass', time: '10:00–16:00' },
  KVÄLL: { label: 'Kvällspass', time: '16:00–22:00' },
}

const SHIFT_STYLES = {
  DAG: { background: 'rgba(142,116,40,0.12)', color: 'var(--gold-on-light)', border: '1px solid rgba(142,116,40,0.3)' },
  KVÄLL: { background: 'rgba(61,53,46,0.08)', color: 'var(--text-dark)', border: '1px solid rgba(61,53,46,0.2)' },
}

const DAY_LABELS = ['Måndag', 'Tisdag', 'Onsdag', 'Torsdag', 'Fredag', 'Lördag', 'Söndag']
const DAY_SHORT = ['Mån', 'Tis', 'Ons', 'Tor', 'Fre', 'Lör', 'Sön']
const ROLE_LABELS = { WAITER: 'Servitör', MANAGER: 'Chef' }

function getMonday(date) {
  const d = new Date(date)
  const day = d.getDay()
  const diff = d.getDate() - day + (day === 0 ? -6 : 1)
  d.setDate(diff)
  d.setHours(0, 0, 0, 0)
  return d
}

function toDateStr(date) {
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  return `${y}-${m}-${d}`
}

function getWeekOptions() {
  const thisMonday = getMonday(new Date())
  return Array.from({ length: 8 }, (_, i) => {
    const d = new Date(thisMonday)
    d.setDate(d.getDate() + (i - 2) * 7)
    return toDateStr(d)
  })
}

function getWeekNumber(date) {
  const d = new Date(date)
  d.setDate(d.getDate() + 3 - (d.getDay() + 6) % 7)
  const week1 = new Date(d.getFullYear(), 0, 4)
  return 1 + Math.round(((d - week1) / 86400000 - 3 + (week1.getDay() + 6) % 7) / 7)
}

function formatWeekLabel(mondayStr) {
  const mon = new Date(mondayStr + 'T12:00:00')
  const sun = new Date(mon)
  sun.setDate(sun.getDate() + 6)
  const weekNum = getWeekNumber(mon)
  const monStr = mon.toLocaleDateString('sv-SE', { day: 'numeric', month: 'short' })
  const sunStr = sun.toLocaleDateString('sv-SE', { day: 'numeric', month: 'short' })
  return `Vecka ${weekNum} (${monStr}–${sunStr})`
}

// konverterar API-skift (startTime/endTime) till lokalt format { id, employeeId, dayOfWeek, shiftType }
function apiToLocal(shift, index) {
  const start = new Date(shift.startTime)
  const dayOfWeek = (start.getDay() + 6) % 7  // 0=Mån ... 6=Sön
  const shiftType = start.getHours() < 14 ? 'DAG' : 'KVÄLL'
  return { id: shift.shiftId ?? `tmp-${index}`, employeeId: shift.employeeId, dayOfWeek, shiftType }
}

const selectStyle = {
  padding: '0.4rem 0.75rem',
  border: '1px solid var(--border)',
  background: 'white',
  fontSize: '0.9rem',
  cursor: 'pointer',
  fontFamily: 'Montserrat, sans-serif',
  fontWeight: 300,
}

const iconBtnStyle = {
  background: 'none',
  border: 'none',
  cursor: 'pointer',
  fontSize: '0.8rem',
  padding: '0 0.1rem',
  color: 'var(--text-muted-on-light)',
  lineHeight: 1,
}

function SchemaAdmin() {
  const weekOptions = getWeekOptions()
  const todayMonday = toDateStr(getMonday(new Date()))

  const [selectedWeek, setSelectedWeek] = useState(todayMonday)
  const [employees, setEmployees] = useState([])
  const [localShifts, setLocalShifts] = useState([])
  const [loading, setLoading] = useState(true)
  const [duplicated, setDuplicated] = useState(false)

  const [editingId, setEditingId] = useState(null)
  const [editType, setEditType] = useState('')

  const [addForm, setAddForm] = useState({ employeeId: '', dayOfWeek: '', shiftType: '' })
  const [addError, setAddError] = useState('')

  // används för att hålla kvar skiften vid duplicering (skippar API-anrop)
  const pendingShifts = useRef(null)

  useEffect(() => {
    if (pendingShifts.current) {
      setLocalShifts(pendingShifts.current)
      pendingShifts.current = null
      setLoading(false)
      setDuplicated(true)
      return
    }
    setLoading(true)
    setDuplicated(false)
    setEditingId(null)
    Promise.all([getEmployees(), getShiftsForWeek(selectedWeek)])
      .then(([emps, sh]) => {
        setEmployees(emps)
        setLocalShifts(sh.map(apiToLocal))
        setLoading(false)
      })
      .catch(() => setLoading(false))
  }, [selectedWeek])

  function getShiftsForCell(employeeId, dayOfWeek) {
    return localShifts.filter(s => s.employeeId === employeeId && s.dayOfWeek === dayOfWeek)
  }

  async function handleDelete(id) {
    try { await deleteShift(id) } catch {}
    setLocalShifts(prev => prev.filter(s => s.id !== id))
    if (editingId === id) setEditingId(null)
  }

  function handleEditStart(shift) {
    setEditingId(shift.id)
    setEditType(shift.shiftType)
  }

  async function handleEditSave(id) {
    const shift = localShifts.find(s => s.id === id)
    if (!shift) return
    try {
      await updateShift(id, shift.employeeId, shift.dayOfWeek, editType, selectedWeek)
    } catch {}
    setLocalShifts(prev => prev.map(s => s.id === id ? { ...s, shiftType: editType } : s))
    setEditingId(null)
  }

  async function handleAdd() {
    setAddError('')
    const { employeeId, dayOfWeek, shiftType } = addForm
    if (!employeeId || dayOfWeek === '' || !shiftType) {
      setAddError('Fyll i alla fält')
      return
    }
    const empId = Number(employeeId)
    const day = Number(dayOfWeek)
    const exists = localShifts.find(s => s.employeeId === empId && s.dayOfWeek === day && s.shiftType === shiftType)
    if (exists) {
      setAddError('Det passet finns redan för den anställde den dagen')
      return
    }
    try {
      const created = await addShift(empId, day, shiftType, selectedWeek)
      setLocalShifts(prev => [...prev, apiToLocal(created, created.shiftId)])
    } catch {
      // fallback: lägg till lokalt om API inte svarar
      setLocalShifts(prev => [...prev, { id: Date.now(), employeeId: empId, dayOfWeek: day, shiftType }])
    }
    setAddForm(f => ({ ...f, shiftType: '' }))
  }

  async function handleDuplicate() {
    const nextMon = new Date(selectedWeek + 'T00:00:00')
    nextMon.setDate(nextMon.getDate() + 7)
    const nextWeek = toDateStr(nextMon)
    // spara skiften till backend för nästa vecka
    const results = await Promise.all(
      localShifts.map(s => addShift(s.employeeId, s.dayOfWeek, s.shiftType, nextWeek).catch(() => null))
    )
    const saved = results.filter(Boolean).map(apiToLocal)
    // fallback till lokalt state om API inte svarar
    pendingShifts.current = saved.length > 0 ? saved : localShifts.map(s => ({ ...s, id: Date.now() + Math.random() }))
    setSelectedWeek(nextWeek)
  }

  if (loading) return <p style={{ padding: '2rem' }}>Laddar...</p>

  return (
    <>
      <div className="main-header">
        <h1>Veckans <em>Schema</em></h1>
      </div>

      <div className="card">
        <div className="card-header">
          <h3>Schemaöversikt</h3>
          <div style={{ display: 'flex', gap: '0.75rem', alignItems: 'center', flexWrap: 'wrap' }}>
            <select value={selectedWeek} onChange={e => setSelectedWeek(e.target.value)} style={selectStyle}>
              {weekOptions.map(w => <option key={w} value={w}>{formatWeekLabel(w)}</option>)}
            </select>
            <button
              className="btn btn-gold btn-sm"
              onClick={handleDuplicate}
              disabled={localShifts.length === 0}
              title={localShifts.length === 0 ? 'Inga pass att duplicera' : 'Kopiera schemat till nästa vecka'}
            >
              Duplicera till nästa vecka
            </button>
          </div>
        </div>

        {duplicated && (
          <p style={{ color: 'var(--success)', fontSize: '0.85rem', marginBottom: '1rem' }}>
            Schemat duplicerades till nästa vecka.
          </p>
        )}

        <div style={{ overflowX: 'auto' }}>
          <table className="admin-table">
            <thead>
              <tr>
                <th>Anställd</th>
                <th>Roll</th>
                {DAY_SHORT.map(d => <th key={d} style={{ textAlign: 'center' }}>{d}</th>)}
                <th style={{ textAlign: 'center' }}>Pass</th>
              </tr>
            </thead>
            <tbody>
              {employees.length === 0 ? (
                <tr>
                  <td colSpan={10} style={{ textAlign: 'center', color: 'var(--text-muted-on-light)', padding: '1.5rem' }}>
                    Inga anställda
                  </td>
                </tr>
              ) : employees.map(emp => (
                <tr key={emp.employeeId}>
                  <td><strong>{emp.firstName} {emp.lastName}</strong></td>
                  <td style={{ color: 'var(--text-muted-on-light)', fontSize: '0.82rem' }}>
                    {ROLE_LABELS[emp.role] || emp.role}
                  </td>
                  {Array.from({ length: 7 }, (_, dayIdx) => {
                    const dayShifts = getShiftsForCell(emp.employeeId, dayIdx)
                    return (
                      <td key={dayIdx} style={{ textAlign: 'center', padding: '0.5rem 0.4rem' }}>
                        {dayShifts.length === 0 ? (
                          <span style={{ color: 'var(--text-muted-on-light)', fontSize: '0.85rem' }}>–</span>
                        ) : dayShifts.map(shift => (
                          <div key={shift.id} style={{ marginBottom: dayShifts.length > 1 ? '0.25rem' : 0 }}>
                            {editingId === shift.id ? (
                              <div style={{ display: 'flex', gap: '0.2rem', alignItems: 'center', justifyContent: 'center' }}>
                                <select
                                  value={editType}
                                  onChange={e => setEditType(e.target.value)}
                                  style={{ fontSize: '0.75rem', padding: '0.15rem 0.3rem', border: '1px solid var(--border-gold)', fontFamily: 'Montserrat, sans-serif' }}
                                >
                                  {Object.entries(SHIFT_TYPES).map(([val, t]) => (
                                    <option key={val} value={val}>{t.label}</option>
                                  ))}
                                </select>
                                <button onClick={() => handleEditSave(shift.id)} style={{ ...iconBtnStyle, color: 'var(--success)' }} title="Spara">✓</button>
                                <button onClick={() => setEditingId(null)} style={{ ...iconBtnStyle, color: 'var(--danger)' }} title="Avbryt">✕</button>
                              </div>
                            ) : (
                              <span style={{ display: 'inline-flex', alignItems: 'center', gap: '0.2rem', padding: '0.15rem 0.45rem', borderRadius: '3px', fontSize: '0.75rem', fontWeight: 400, whiteSpace: 'nowrap', ...SHIFT_STYLES[shift.shiftType] }}>
                                {SHIFT_TYPES[shift.shiftType].label}
                                <button onClick={() => handleEditStart(shift)} style={iconBtnStyle} title="Redigera">✏</button>
                                <button onClick={() => handleDelete(shift.id)} style={{ ...iconBtnStyle, color: 'var(--danger)' }} title="Ta bort">×</button>
                              </span>
                            )}
                          </div>
                        ))}
                      </td>
                    )
                  })}
                  <td style={{ textAlign: 'center', fontWeight: 600, color: 'var(--gold-on-light)' }}>
                    {localShifts.filter(s => s.employeeId === emp.employeeId).length}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Lägg till pass */}
      <div className="card">
        <div className="card-header" style={{ marginBottom: '1rem' }}>
          <h3>Lägg till pass</h3>
        </div>
        <div className="inline-form" style={{ margin: 0, border: 'none', padding: 0 }}>
          <div className="form-row" style={{ gridTemplateColumns: '1fr 1fr 1fr auto', alignItems: 'end', gap: '1rem' }}>
            <div>
              <label>Anställd</label>
              <select value={addForm.employeeId} onChange={e => setAddForm(f => ({ ...f, employeeId: e.target.value }))}>
                <option value="">Välj anställd</option>
                {employees.map(e => (
                  <option key={e.employeeId} value={e.employeeId}>{e.firstName} {e.lastName}</option>
                ))}
              </select>
            </div>
            <div>
              <label>Dag</label>
              <select value={addForm.dayOfWeek} onChange={e => setAddForm(f => ({ ...f, dayOfWeek: e.target.value }))}>
                <option value="">Välj dag</option>
                {DAY_LABELS.map((d, i) => <option key={i} value={i}>{d}</option>)}
              </select>
            </div>
            <div>
              <label>Pass</label>
              <select value={addForm.shiftType} onChange={e => setAddForm(f => ({ ...f, shiftType: e.target.value }))}>
                <option value="">Välj pass</option>
                {Object.entries(SHIFT_TYPES).map(([val, t]) => (
                  <option key={val} value={val}>{t.label} ({t.time})</option>
                ))}
              </select>
            </div>
            <div>
              <button className="btn btn-gold" onClick={handleAdd} style={{ width: '100%' }}>
                Lägg till
              </button>
            </div>
          </div>
          {addError && <p style={{ color: 'var(--danger)', fontSize: '0.82rem', marginTop: '0.5rem' }}>{addError}</p>}
        </div>
      </div>
    </>
  )
}

export default SchemaAdmin
