-- Sincroniza las migraciones con el schema que producción ya tiene (aplicado con db push).
-- En producción NO se ejecuta: se registra con
--   prisma migrate resolve --applied 20261115120000_sync_schema

-- AlterTable
ALTER TABLE "Progress" ADD COLUMN     "revealedIndices" INTEGER[] DEFAULT ARRAY[]::INTEGER[];

-- AlterTable
ALTER TABLE "User" ADD COLUMN     "username" TEXT,
ALTER COLUMN "neuronas" SET DEFAULT 50;

-- CreateTable
CREATE TABLE "Setting" (
    "id" TEXT NOT NULL,
    "value" TEXT NOT NULL,

    CONSTRAINT "Setting_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "GameMessage" (
    "id" TEXT NOT NULL,
    "content" TEXT NOT NULL,
    "type" TEXT NOT NULL,

    CONSTRAINT "GameMessage_pkey" PRIMARY KEY ("id")
);

-- CreateIndex
CREATE UNIQUE INDEX "User_username_key" ON "User"("username");
