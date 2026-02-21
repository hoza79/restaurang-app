import { lunchWeek, carteMenu, artists, bookings } from '../data/mockData';

const DAY_NAMES = ['Måndag', 'Tisdag', 'Onsdag', 'Torsdag', 'Fredag'];

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
    .map(Number)
    .sort()
    .map(dayNum => ({
      dayNum,
      label: DAY_NAMES[dayNum - 1] || `Dag ${dayNum}`,
      items: byDay[dayNum],
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
