import { PrismaAdapter } from "@auth/prisma-adapter";
import { NextAuthOptions } from "next-auth";
import GoogleProvider from "next-auth/providers/google";
import prisma from "./prisma";

export const authOptions: NextAuthOptions = {
  adapter: PrismaAdapter(prisma) as any,
  providers: [
    GoogleProvider({
      clientId: process.env.GOOGLE_CLIENT_ID!,
      clientSecret: process.env.GOOGLE_CLIENT_SECRET!,
    }),
  ],
  callbacks: {
    async session({ session, user }: { session: any; user: any }) {
      if (session.user) {
        // Pass extra fields from DB to session
        const dbUser = await prisma.user.findUnique({
          where: { id: user.id },
          select: { points: true, neuronas: true, levelId: true }
        });
        
        session.user.id = user.id;
        session.user.points = dbUser?.points ?? 0;
        session.user.neuronas = dbUser?.neuronas ?? 150;
        session.user.levelId = dbUser?.levelId ?? 1;
      }
      return session;
    },
  },
  pages: {
    signIn: '/auth/signin',
  },
};
