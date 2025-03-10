-- Удаляем ограничение CHECK для pressure
ALTER TABLE reservoirs DROP CONSTRAINT IF EXISTS reservoirs_pressure_check;
