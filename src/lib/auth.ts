import { PrismaAdapter } from "@auth/prisma-adapter";
import type { NextAuthOptions } from "next-auth";
import type { Adapter } from "next-auth/adapters";
import GoogleProvider from "next-auth/providers/google";
import prisma from "./prisma";

export const authOptions: NextAuthOptions = {
  // @auth/prisma-adapter tipa contra @auth/core; en runtime es compatible con next-auth v4.
  adapter: PrismaAdapter(prisma) as unknown as Adapter,
  providers: [
    GoogleProvider({
      clientId: process.env.GOOGLE_CLIENT_ID!,
      clientSecret: process.env.GOOGLE_CLIENT_SECRET!,
    }),
  ],
  callbacks: {
    async session({ session, user }) {
      if (session.user) {
        // Pass extra fields from DB to session
        const dbUser = await prisma.user.findUnique({
          where: { id: user.id },
          select: { points: true, neuronas: true, levelId: true }
        });
        
        // Nunca se expone User.id al navegador (constitución, principio 2; AUTH-08).
        session.user.points = dbUser?.points ?? 0;
        session.user.neuronas = dbUser?.neuronas ?? 50;
        session.user.levelId = dbUser?.levelId ?? 1;
      }
      return session;
    },
  },
  pages: {
    signIn: '/auth/signin',
  },
};
