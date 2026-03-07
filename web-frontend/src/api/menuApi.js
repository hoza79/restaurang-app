import { lunchWeek, carteMenu, artists } from '../data/mockData';

const DAY_NAMES = ['Måndag', 'Tisdag', 'Onsdag', 'Torsdag', 'Fredag'];

// tar ut veckodagen från datumsträngen
function getDayLabel(dateStr) {
  const date = new Date(dateStr + 'T12:00:00');
  const day = date.getDay(); // 0=sön, 1=mån ... 5=fre, 6=lör
  return DAY_NAMES[day - 1] || dateStr;
}

// gör om API-svaret till dagslista
function parseLunchData(data) {
  const byDay = {};
  for (const item of data.Items) {
    const day = item.mealDay;
    if (!byDay[day]) byDay[day] = [];
    byDay[day].push({
      id: item.menuItemId,
      name: item.name,
      description: item.description,
      price: item.price,
      available: item.available,
    });
  }

  return Object.keys(byDay)
    .sort()
    .map((dateStr, i) => ({
      dayNum: i + 1,
      label: getDayLabel(dateStr),
      date: dateStr,
      items: byDay[dateStr],
    }));
}

// fallback om backend inte körs
function getMockLunch() {
  return lunchWeek.days.map((day, i) => ({
    dayNum: i + 1,
    label: day.label,
    date: null,
    items: day.items,
  }));
}

export async function getLunchMenu() {
  let days;
  let source = 'mock';
  try {
    const res = await fetch('/api/lunch');
    if (!res.ok) throw new Error('API svarade inte');
    const data = await res.json();
    days = parseLunchData(data);
    source = 'api';
  } catch {
    days = getMockLunch();
  }

  return {
    includes: 'Alla luncher inkluderar salladsbuffé, bröd, smör, valfri dryck och kaffe',
    days,
    source,
  };
}

// POST ny lunchrätt, availableDate = "YYYY-MM-DD"
export async function addLunchItem({ name, description, price, availableDate }) {
  const res = await fetch('/api/lunch', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ name, description, price: parseFloat(price), availableDate }),
  });
  if (!res.ok) throw new Error('Kunde inte lägga till rätten');
}

// tar bort en lunchrätt via id
export async function deleteLunchItem(id) {
  const res = await fetch(`/api/lunch/${id}`, { method: 'DELETE' });
  if (!res.ok) throw new Error('Kunde inte ta bort rätten');
}

// uppdaterar en lunchrätt via id
export async function updateLunchItem(id, { name, description, price, availableDate }) {
  const res = await fetch(`/api/lunch/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ name, description, price: parseFloat(price), availableDate }),
  });
  if (!res.ok) throw new Error('Kunde inte uppdatera rätten');
}

// gör om /api/menu-svaret till samma format som mockData carteMenu
function parseCarteData(data) {
  const map = [
    { key: 'appetizers',  label: 'Förrätt' },
    { key: 'mainCourses', label: 'Huvudrätt' },
    { key: 'desserts',    label: 'Efterrätt' },
    { key: 'drinks',      label: 'Dryck' },
  ];
  return map
    .map(({ key, label }) => ({
      category: label,
      items: (data[key] || []).map(i => ({
        id: i.menuItemId,
        name: i.name,
        description: i.description,
        price: i.price,
        options: i.options ?? false,
      })),
    }))
    .filter(cat => cat.items.length > 0);
}

export async function getCarteMenu() {
  try {
    const res = await fetch('/api/menu');
    if (!res.ok) throw new Error('API svarade inte');
    const data = await res.json();
    return parseCarteData(data);
  } catch {
    return carteMenu;
  }
}

// kategorinamn på svenska -> enum-värde som backend förväntar sig
const CATEGORY_ENUM = {
  'Förrätt':   'appetizers',
  'Huvudrätt': 'maincourses',
  'Efterrätt': 'desserts',
  'Dryck':     'drinks',
}

// lägger till en rätt i en kategori, category = t.ex. "Förrätt"
export async function addCarteItem(category, { name, description, price, options }) {
  const enumVal = CATEGORY_ENUM[category]
  if (!enumVal) throw new Error('Okänd kategori: ' + category)
  const res = await fetch(`/api/menu/${enumVal}`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ name, description, price: parseFloat(price), options: options ?? false }),
  })
  if (!res.ok) throw new Error('Kunde inte lägga till rätten')
}

// tar bort en carte-rätt via id
export async function deleteCarteItem(id) {
  const res = await fetch(`/api/menu/${id}`, { method: 'DELETE' })
  if (!res.ok) throw new Error('Kunde inte ta bort rätten')
}

// uppdaterar en carte-rätt via id
export async function updateCarteItem(id, { name, description, price, options }) {
  const res = await fetch(`/api/menu/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ name, menuItemId: id, description, price: parseFloat(price), options: options ?? false }),
  })
  if (!res.ok) throw new Error('Kunde inte uppdatera rätten')
}

// hämtar alla bord
export async function getTables() {
  try {
    const res = await fetch('/api/tables')
    if (!res.ok) throw new Error('API svarade inte')
    return await res.json()
  } catch {
    return []
  }
}

// hämtar alla musikevenemang, fallback till mockData
export async function getMusicEvents() {
  try {
    const res = await fetch('/api/music')
    if (!res.ok) throw new Error('API svarade inte')
    return await res.json()
  } catch {
    return artists.map(a => ({
      id: a.id,
      title: a.name,
      description: a.description,
      date: null,
      imgPath: a.image || null,
    }))
  }
}

// POST nytt musikevenemang
export async function addMusicEvent({ title, description, date, imgPath }) {
  const res = await fetch('/api/music', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ title, description, date, imgPath }),
  })
  if (!res.ok) throw new Error('Kunde inte lägga till evenemanget')
}

// tar bort ett musikevenemang via id
export async function deleteMusicEvent(eventId) {
  const res = await fetch(`/api/music/${eventId}`, { method: 'DELETE' })
  if (!res.ok) throw new Error('Kunde inte ta bort evenemanget')
}

// uppdaterar ett musikevenemang via id
export async function updateMusicEvent(eventId, { title, description, date, imgPath }) {
  const res = await fetch(`/api/music/${eventId}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ title, description, date, imgPath }),
  })
  if (!res.ok) throw new Error('Kunde inte uppdatera evenemanget')
}

// hämtar alla bokningar, fallback till tom lista
export async function getBookings() {
  try {
    const res = await fetch('/api/bookings')
    if (!res.ok) throw new Error('API svarade inte')
    return await res.json()
  } catch {
    return []
  }
}

// POST ny bokning
export async function addBooking({ firstName, lastName, phoneNumber, guestCount, date, tableId }) {
  const res = await fetch('/api/bookings', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ firstName, lastName, phoneNumber, guestCount, date, tableId: tableId || null, tableNumber: null }),
  })
  if (!res.ok) throw new Error('Kunde inte lägga till bokningen')
}

// tar bort en bokning via id
export async function deleteBooking(bookingId) {
  const res = await fetch(`/api/bookings/${bookingId}`, { method: 'DELETE' })
  if (!res.ok) throw new Error('Kunde inte ta bort bokningen')
}

// uppdaterar en bokning via id
export async function updateBooking(bookingId, { firstName, lastName, phoneNumber, guestCount, date, tableId }) {
  const res = await fetch(`/api/bookings/${bookingId}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ firstName, lastName, phoneNumber, guestCount, date, tableId: tableId ?? null, tableNumber: null }),
  })
  if (!res.ok) throw new Error('Kunde inte uppdatera bokningen')
}

export async function createBooking(data) {
  return addBooking(data)
}

// mock-anställda för schema (används som fallback tills backend finns)
const mockScheduleEmployees = [
  { employeeId: 1, firstName: 'Anna', lastName: 'Lindström', role: 'WAITER' },
  { employeeId: 2, firstName: 'Björn', lastName: 'Karlsson', role: 'MANAGER' },
  { employeeId: 3, firstName: 'Cecilia', lastName: 'Berg', role: 'WAITER' },
  { employeeId: 4, firstName: 'David', lastName: 'Svensson', role: 'WAITER' },
]

// genererar mock-skift med dagspass (10-16) och kvällspass (16-22)
function generateMockShifts(weekStart) {
  const monday = new Date(weekStart + 'T00:00:00')
  const shifts = []
  let id = 1
  // { empId, day (0=mån), type: 'DAG'|'KVÄLL' }
  const schedules = [
    { empId: 1, day: 0, type: 'DAG' },   { empId: 1, day: 2, type: 'DAG' },   { empId: 1, day: 4, type: 'KVÄLL' },
    { empId: 2, day: 0, type: 'DAG' },   { empId: 2, day: 1, type: 'DAG' },   { empId: 2, day: 2, type: 'DAG' },
    { empId: 2, day: 3, type: 'KVÄLL' }, { empId: 2, day: 4, type: 'KVÄLL' },
    { empId: 3, day: 1, type: 'KVÄLL' }, { empId: 3, day: 3, type: 'DAG' },   { empId: 3, day: 5, type: 'KVÄLL' },
    { empId: 4, day: 0, type: 'KVÄLL' }, { empId: 4, day: 1, type: 'DAG' },   { empId: 4, day: 5, type: 'DAG' },   { empId: 4, day: 6, type: 'KVÄLL' },
  ]
  for (const { empId, day, type } of schedules) {
    const startHour = type === 'DAG' ? 10 : 16
    const endHour   = type === 'DAG' ? 16 : 22
    const start = new Date(monday)
    start.setDate(start.getDate() + day)
    start.setHours(startHour, 0, 0, 0)
    const end = new Date(monday)
    end.setDate(end.getDate() + day)
    end.setHours(endHour, 0, 0, 0)
    shifts.push({ shiftId: id++, employeeId: empId, startTime: start.toISOString(), endTime: end.toISOString(), shiftStatus: 'SCHEDULED' })
  }
  return shifts
}

// hämtar alla anställda, fallback till mock
export async function getEmployees() {
  try {
    const res = await fetch('/api/employees')
    if (!res.ok) throw new Error('API svarade inte')
    return await res.json()
  } catch {
    return mockScheduleEmployees
  }
}

// hämtar skift för en vecka (weekStart = "YYYY-MM-DD" för måndag), fallback till mock
export async function getShiftsForWeek(weekStart) {
  try {
    const res = await fetch(`/api/shifts?week=${weekStart}`)
    if (!res.ok) throw new Error('API svarade inte')
    return await res.json()
  } catch {
    return generateMockShifts(weekStart)
  }
}
