import { lunchWeek, carteMenu, artists, bookings } from '../data/mockData';

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
export async function addCarteItem(category, { name, description, price, available }) {
  const enumVal = CATEGORY_ENUM[category]
  if (!enumVal) throw new Error('Okänd kategori: ' + category)
  const res = await fetch(`/api/menu/${enumVal}`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ name, description, price: parseFloat(price), available: available ?? true }),
  })
  if (!res.ok) throw new Error('Kunde inte lägga till rätten')
}

// tar bort en carte-rätt via id
export async function deleteCarteItem(id) {
  const res = await fetch(`/api/menu/${id}`, { method: 'DELETE' })
  if (!res.ok) throw new Error('Kunde inte ta bort rätten')
}

export async function getArtists() {
  // inget API ännu
  return artists;
}

export async function getBookings() {
  // inget API ännu
  return bookings;
}

export async function createBooking(data) {
  // inget API ännu
  console.log('Booking submitted (mock):', data);
  return { success: true };
}
