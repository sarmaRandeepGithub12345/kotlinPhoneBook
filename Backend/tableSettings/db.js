import {db} from "../index.js";


export const performFunction = async (text, params) => {
  //console.log(text,params)
  // const poolConfig = {
  //   connectionString:  process.env.DATABASE_URL,
  //   max: 20,
  //   idleTimeoutMillis: 70000,
  //   connectionTimeoutMillis: 2000,
  // };
  // const db = new pg.Pool(poolConfig);
  const client = await db.connect();
  try {
    const result = await client.query(text, params);
    
    return result.rows;
  } catch (error) {
    throw error;
  } finally {
    if (client) {
      client.release();
    }
  }
};
