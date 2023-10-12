import pg from "pg";
import dotenv from "dotenv";
dotenv.config();
const poolConfig = {
    connectionString: process.env.DATABASE_URL,
    max: 20,
    idleTimeoutMillis: 30000,
    connectionTimeoutMillis: 2000,
  };
  const db = new pg.Pool(poolConfig);
   
  const teachers = `
  create table if not exists
    teachers (
      id uuid default uuid_generate_v4 () not null primary key,
      email text not null,
      password text not null
    );
  `;
  const candidates = `
  create table if not exists 
    candidates (
      id uuid default uuid_generate_v4 () not null primary key,
      loggerId uuid default uuid_generate_v4 () not null references teachers(id),
      firstname text not null,
      lastname text not null,
      phone text not null,
      roll text not null
   );
  `;
  
  db.query(teachers, (err, res) => {
    try {
      console.log(res,"teacher");
    } catch (error) {
      console.log(err);
    }
  });
  db.query(candidates, (err, res) => {
      try {
        //console.log(res,"teacher");
      } catch (error) {
        console.log(err);
      }
    });
  await db.end();
