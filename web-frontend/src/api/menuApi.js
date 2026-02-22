import { lunchWeek, carteMenu, artists, bookings } from '../data/mockData';

const DAY_NAMES = ['Måndag', 'Tisdag', 'Onsdag', 'Torsdag', 'Fredag'];

// räknar ut dagnamn från ett ISO-datum ("2026-02-23" → "Måndag")
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
      items: byDay[dateStr],
    }));
}

// fallback om backend inte körs
function getMockLunch() {
  return lunchWeek.days.map((day, i) => ({
    dayNum: i + 1,
    label: day.label,
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

export async function getCarteMenu() {
  // inget API ännu
  return carteMenu;
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

// skickar ny lunchrätt till backend
// availableDate ska vara "YYYY-MM-DD", t.ex. "2026-02-24"
export async function addLunchItem({ name, description, price, availableDate }) {
  const res = await fetch('/api/lunch', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ name, description, price: parseFloat(price), availableDate }),
  })
  if (!res.ok) throw new Error('Kunde inte lägga till rätten')
}
