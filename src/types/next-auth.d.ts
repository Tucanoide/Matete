import { DefaultSession } from "next-auth";

declare module "next-auth" {
  interface Session {
    user: {
      points: number;
      neuronas: number;
      levelId: number;
    } & DefaultSession["user"];
  }

  interface User {
    points: number;
    neuronas: number;
    levelId: number;
  }
}
