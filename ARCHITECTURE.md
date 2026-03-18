# Architecture: Pers-Matete

## Tech Stack
- **Frontend**: Next.js (App Router), React, Tailwind CSS, Framer Motion (Animations).
- **Backend/ORM**: Server Actions, Prisma.
- **Database**: PostgreSQL (Prisma Provider).
- **Authentication**: NextAuth.js (Prisma)
- **Testing**: Vitest + React Testing Library.

## Key Components
- **GameEngine**: Core client component handling game logic, keyboard input, and UI updates.
- **Server Actions**: Located in `src/app/actions/`, handle DB interactions for word solving, hints, and level transitions.

## Data Model
- **User**: Stores points, neuronas, current level, and total time. Can be authenticated (email) or anonymous (guest cookie).
- **Word**: Contains words, descriptions, and metadata.
- **Level**: Groups words and defines costs for hints/skips.
- **Progress**: Junction table between User and Word to track solved/skipped words.

## Performance & Optimization (Hostinger)
- **Singleton Prisma**: Instance managed in `src/lib/prisma.ts`.
- **Server Components**: Used for initial data fetching where possible.
- **Minimal Processes**: Designed to respect Hostinger's concurrent process limits.
