export const menuItems = {
  MY_EVENTS: { name: "Tornei", link: "/my-events" },
  RANKED: { name: "Classifica", link: "/rankings" },
  LEAGUES: { 
    name: "Leghe", 
    link: "#",
    subcategories: {
      NEW: { name: "Crea", link: "/leagues/new" },
      ALL_LEAGUES: { name: "Tutte", link: "/leagues" },
      MY_LEAGUES: { name: "Iscrizioni", link: "/leagues/my-subs" },
    }
  },
  COUNTER: { name: "Segnapunti", link: "/counter" },
  CURRENT: { name: "Match corrente", link: "/current" },
  AUTH: { name: "Accedi", link: "/auth" },
  LOGOUT: { name: "Esci", link: "/home" },
};
