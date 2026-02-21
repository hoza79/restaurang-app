export const lunchWeek = {
  weekNumber: 7,
  weekLabel: 'Vecka 7 - 10-14 Februari',
  soup: { name: 'Svampsoppa med timjan och grädde', price: 115 },
  includes: 'Alla luncher inkluderar salladsbuffé, bröd, smör, valfri dryck och kaffe',
  days: [
    {
      key: 'mandag',
      label: 'Måndag',
      date: 'Måndag 10 februari',
      items: [
        { id: 1, name: 'Köttbullar med gräddsås', description: 'Hemgjorda köttbullar med pressgurka, lingon och krämig potatispuré', price: 135 },
        { id: 2, name: 'Stekt strömming', description: 'Med potatismos, skirat smör och lingon, en klassiker', price: 135 },
        { id: 3, name: 'Rostad rotfruktsgratäng', description: 'Veckans vegetariska, med örtcrème och surdegsbröd', price: 125 },
      ]
    },
    {
      key: 'tisdag',
      label: 'Tisdag',
      date: 'Tisdag 11 februari',
      items: [
        { id: 4, name: 'Ärtsoppa med pannkakor', description: 'Klassisk gul ärtsoppa serverad med tunna pannkakor och sylt', price: 125 },
        { id: 5, name: 'Fläskschnitzel', description: 'Panerad schnitzel med citron, kapris och stekt potatis', price: 135 },
        { id: 6, name: 'Böngryta med fetaost', description: 'Kryddig böngryta med fetaost, örter och varmt bröd', price: 125 },
      ]
    },
    {
      key: 'onsdag',
      label: 'Onsdag',
      date: 'Onsdag 12 februari',
      items: [
        { id: 7, name: 'Kalops', description: 'Långkokt nötkött med morötter, kryddpeppar och kokt potatis', price: 135 },
        { id: 8, name: 'Ugnsbakad lax', description: 'Med dillsås, pressad potatis och grönsaker', price: 145 },
        { id: 9, name: 'Svamppasta', description: 'Krämig pasta med skogschampinjoner, vitlök och parmesan', price: 125 },
      ]
    },
    {
      key: 'torsdag',
      label: 'Torsdag',
      date: 'Torsdag 13 februari',
      items: [
        { id: 10, name: 'Pannbiff med lök', description: 'Hemlagad pannbiff med brynt lök, gräddsås och potatis', price: 135 },
        { id: 11, name: 'Torskgratäng', description: 'Med räkor, dill och krämig potatismos', price: 140 },
        { id: 12, name: 'Grönsakscurry', description: 'Mild curry med säsongens grönsaker, kokosmjölk och ris', price: 125 },
      ]
    },
    {
      key: 'fredag',
      label: 'Fredag',
      date: 'Fredag 14 februari',
      items: [
        { id: 13, name: 'Sjömansbiff', description: 'Klassisk sjömansbiff med potatis, lök och öl', price: 135 },
        { id: 14, name: 'Fish & chips', description: 'Öldegsfriterad torsk med pommes, ärtkräm och remoulad', price: 140 },
        { id: 15, name: 'Vegetarisk paj', description: 'Smördegspaj med spenat, getost och soltorkade tomater', price: 125 },
      ]
    },
  ]
};

export const carteMenu = [
  {
    category: 'Förrätt',
    items: [
      { id: 101, name: 'Toast Skagen', description: 'Handskalade räkor, löjrom och dill på rostat surdegsbröd', price: 145 },
      { id: 102, name: 'Svamptoast', description: 'Säsongens svamp, brynt smör och pocherat ägg', price: 125 },
      { id: 103, name: 'Gravad älg', description: 'Med senapsås, pepparrot och kavring', price: 155 },
    ]
  },
  {
    category: 'Huvudrätt',
    items: [
      { id: 104, name: 'Rådjursstek', description: 'Med svartvinbärssås, rostad potatis och grönkål', price: 295 },
      { id: 105, name: 'Helstekt gös', description: 'Brynt smör, kapris, citron och dillkokt potatis', price: 275 },
      { id: 106, name: 'Wallenbergare', description: 'Kalvfärsbiff med puré, ärtor och lingon', price: 245 },
      { id: 107, name: 'Svamprisotto', description: 'Kantareller, parmesan och tryffelolja', price: 225 },
    ]
  },
  {
    category: 'Efterrätt',
    items: [
      { id: 108, name: 'Äppelkaka', description: 'Med vaniljsås och kanel', price: 95 },
      { id: 109, name: 'Chokladfondant', description: 'Med hallonsorbet och mynta', price: 115 },
    ]
  },
  {
    category: 'Dryck',
    items: [
      { id: 110, name: 'Husets rödvin', description: 'Glas - fråga personalen om flaskpriser', price: 125 },
      { id: 111, name: 'Husets vitvin', description: 'Glas - fråga personalen om flaskpriser', price: 115 },
    ]
  }
];

export const artists = [
  {
    id: 1,
    name: 'Karin Ström Trio',
    genre: 'Jazz & Bossa nova',
    description: 'Mjuk jazz och brasilianska toner, perfekt för en romantisk kväll',
    date: { day: 14, month: 'Februari', weekday: 'Fredag' },
    time: 'Kl. 19:00',
  },
  {
    id: 2,
    name: 'Folkton',
    genre: 'Svensk folkmusik',
    description: 'Nyckelharpa, fiol och sång. Traditionella visor i modern tappning',
    date: { day: 15, month: 'Februari', weekday: 'Lördag' },
    time: 'Kl. 19:30',
  },
  {
    id: 3,
    name: 'Blue River Band',
    genre: 'Blues & Country',
    description: 'Akustisk blues med storytelling, avslappnad fredagskväll',
    date: { day: 21, month: 'Februari', weekday: 'Fredag' },
    time: 'Kl. 20:00',
  },
];

export const bookings = [
  { id: 1, table: 1, name: 'Eriksson', guests: 4, time: '18:00', status: 'booked' },
  { id: 2, table: 2, name: 'Lindqvist', guests: 2, time: '19:00', status: 'booked' },
  { id: 3, table: 3, name: '', guests: 0, time: '', status: 'free' },
  { id: 4, table: 4, name: 'Andersson', guests: 6, time: '18:30', status: 'booked' },
  { id: 5, table: 5, name: '', guests: 0, time: '', status: 'free' },
  { id: 6, table: 6, name: '', guests: 0, time: '', status: 'unavailable' },
  { id: 7, table: 7, name: '', guests: 0, time: '', status: 'unavailable' },
  { id: 8, table: 8, name: '', guests: 0, time: '', status: 'unavailable' },
  { id: 9, table: 9, name: '', guests: 0, time: '', status: 'unavailable' },
  { id: 10, table: 10, name: '', guests: 0, time: '', status: 'unavailable' },
];
