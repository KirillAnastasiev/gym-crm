// Initialize MongoDB user for gym_db database
db = db.getSiblingDB('gym_db');

db.createUser({
  user: 'admin',
  pwd: 'admin',
  roles: [
    {
      role: 'readWrite',
      db: 'gym_db'
    }
  ]
});

