# Rincian Perhitungan Kalkulator Bibit Kentang

## 1. Perhitungan Kebutuhan Bibit (calculateSeeds)

### A. Input Parameter
- `panjangLahan` (L) - dalam meter
- `lebarLahan` (W_field) - dalam meter
- `lebarGuludan` (W_ridge) - dalam cm (default: 80 cm)
- `lebarParit` (W_furrow) - dalam cm
- `jarakTanam` (s) - dalam cm
- `generasiBibit` - G0, G2, atau G3
- `jumlahBibitPerKg` - (opsional) biji per kg
- `estimasiHarga` - (opsional) harga bibit
- `estimasiHargaUnit` - (opsional) kg atau kuintal

### B. Langkah Perhitungan

#### 1. Konversi Unit
```
W_ridge = lebarGuludan / 100  (cm → meter)
W_furrow = lebarParit / 100   (cm → meter)
s = jarakTanam / 100          (cm → meter)
```

#### 2. Menghitung Jumlah Guludan
```
U = W_ridge + W_furrow        (lebar unit tanam dalam meter)
J = floor(W_field / U)        (jumlah guludan yang muat)
sisa = W_field - (J × U)      (sisa lebar lahan)
```

**Pengecekan sisa lahan:**
```
ridgeMin = 0.75 meter
J_final = J + (sisa >= ridgeMin ? 1 : 0)
```
Jika sisa lahan ≥ 75 cm, maka ditambah 1 guludan lagi.

#### 3. Menghitung Populasi Tanaman
```
T_row = floor(L / s) + 1                (tanaman per guludan)
T_pop = J_final × T_row × 1             (total populasi tanaman)
```

**Penjelasan:**
- `floor(L / s)` = jumlah interval jarak tanam
- `+ 1` = ditambah 1 tanaman di awal baris
- Dikalikan dengan jumlah guludan untuk mendapat total populasi

#### 4. Menghitung Kebutuhan Bibit

##### Untuk Generasi G0 (Unit: Biji)
```
Kebutuhan = T_pop biji
```
Tidak perlu konversi ke kg karena G0 dihitung per biji.

##### Untuk Generasi G2/G3 (Unit: Kg)

**Menentukan seeds_per_kg (biji per kg):**

Prioritas sumber data:
1. Input user (`jumlahBibitPerKg`)
2. Database (`seeds_per_kg_min` dan `seeds_per_kg_max`)
3. Nilai default:
   - G2: min=15, max=15
   - G3: min=12, max=18

**Rumus perhitungan:**
```
seeds_per_kg = { min, max }

kg_min = ceil(T_pop / seeds_per_kg.max)
kg_max = ceil(T_pop / seeds_per_kg.min)
kg_est = ceil((kg_min + kg_max) / 2)
```

**Contoh:**
- Total populasi = 10,000 tanaman
- seeds_per_kg = {min: 12, max: 18}
- kg_min = ceil(10000 / 18) = 556 kg
- kg_max = ceil(10000 / 12) = 834 kg
- kg_est = ceil((556 + 834) / 2) = 695 kg

#### 5. Menghitung Estimasi Biaya

**Menentukan harga:**

Prioritas sumber harga:
1. Input user (`estimasiHarga` dan `estimasiHargaUnit`)
2. Database (`price_per_unit_min`, `price_per_unit_max`, `price_unit`)

**Normalisasi harga ke per kg:**
```javascript
if (unit === "kuintal") {
    priceKg = price / 100
} else {
    priceKg = price  // sudah dalam kg
}
```

**Perhitungan biaya:**

- **Harga tunggal (single):**
  ```
  Biaya = priceKg × kg_est
  ```

- **Harga range:**
  ```
  Biaya_min = minPriceKg × kg_est
  Biaya_max = maxPriceKg × kg_est
  ```

### C. Output
```json
{
  "ringkasanLahan": {
    "lebarUnitTanam": "1.20 meter",
    "jumlahGuludan": "8 baris",
    "panjangTanamPerGuludan": "50.00 meter"
  },
  "kebutuhanTanam": {
    "jumlahTanamanPerGuludan": "126 pohon",
    "totalPopulasiTanaman": "1,008 pohon"
  },
  "kebutuhanBibit": {
    "estimasi": "67 kg (0.67 kuintal)",
    "unit": "kg",
    "rangeKg": {
      "kg_min": 56,
      "kg_est": 67,
      "kg_max": 84
    },
    "note": "Angka perkiraan. Kebutuhan bisa lebih sedikit/lebih banyak tergantung ukuran umbi (biji/kg)."
  },
  "estimasiBiaya": {
    "total": "Rp 670,000 - Rp 1,005,000"
  }
}
```

---

## 2. Perhitungan Terbalik (calculateReverseSeeds)

### A. Input Parameter
- `jumlahBibit` - jumlah bibit yang tersedia (kg untuk G2/G3, biji untuk G0)
- `jarakTanam` - dalam cm
- `lebarGuludan` - dalam cm (default: 80 cm)
- `lebarParit` - dalam cm
- `generasiBibit` - G0, G2, atau G3
- `jumlahPerKg` - (wajib untuk G2/G3) estimasi biji per kg

### B. Langkah Perhitungan

#### 1. Konversi Unit
```
jarakTanam = jarakTanam / 100     (cm → meter)
lebarGuludan = lebarGuludan / 100 (cm → meter)
lebarParit = lebarParit / 100     (cm → meter)
```

#### 2. Menghitung Total Tanaman

**Untuk G0:**
```
totalTanaman = jumlahBibit
```

**Untuk G2/G3:**
```
totalTanaman = jumlahBibit × jumlahPerKg
```

**Contoh:**
- Bibit G3 = 100 kg
- jumlahPerKg = 15 biji/kg
- totalTanaman = 100 × 15 = 1,500 tanaman

#### 3. Optimasi Dimensi Lahan

**Konsep:** Mencari layout lahan yang mendekati rasio panjang:lebar = 1.5:1

```
lebarUnitTanam = lebarGuludan + lebarParit
targetRasio = 1.5

jumlahGuludan = round(√(totalTanaman × jarakTanam / (targetRasio × lebarUnitTanam)))
```

**Derivasi rumus:**
```
Misalkan:
- P = panjang lahan
- L = lebar lahan
- n = jumlah guludan
- t = tanaman per guludan
- s = jarak tanam
- u = lebar unit tanam

Maka:
L = n × u
P = t × s
totalTanaman = n × t

Target: P/L ≈ 1.5
→ (t × s) / (n × u) ≈ 1.5
→ t ≈ 1.5 × n × u / s

Karena totalTanaman = n × t:
→ totalTanaman = n × (1.5 × n × u / s)
→ totalTanaman = 1.5 × n² × u / s
→ n² = totalTanaman × s / (1.5 × u)
→ n = √(totalTanaman × s / (1.5 × u))
```

Jika `jumlahGuludan < 1`, maka set ke 1.

#### 4. Menghitung Dimensi Lahan

```
tanamanPerGuludan = ceil(totalTanaman / jumlahGuludan)
panjangPerGuludan = tanamanPerGuludan × jarakTanam
lebarLahan = jumlahGuludan × lebarUnitTanam
estimasiLuasM2 = panjangPerGuludan × lebarLahan
tanamanAktual = jumlahGuludan × tanamanPerGuludan
```

**Contoh perhitungan:**
```
Input:
- jumlahBibit = 100 kg (G3)
- jumlahPerKg = 15
- jarakTanam = 40 cm = 0.4 m
- lebarGuludan = 80 cm = 0.8 m
- lebarParit = 40 cm = 0.4 m

Perhitungan:
totalTanaman = 100 × 15 = 1,500
lebarUnitTanam = 0.8 + 0.4 = 1.2 m
jumlahGuludan = round(√(1500 × 0.4 / (1.5 × 1.2)))
              = round(√(600 / 1.8))
              = round(√333.33)
              = round(18.26)
              = 18 guludan

tanamanPerGuludan = ceil(1500 / 18) = 84 tanaman
panjangPerGuludan = 84 × 0.4 = 33.6 m
lebarLahan = 18 × 1.2 = 21.6 m
estimasiLuas = 33.6 × 21.6 = 725.76 m²
```

### C. Output
```json
{
  "ringkasan": {
    "estimasiLuasM2": "725.8",
    "jumlahGuludan": "18",
    "panjangPerGuludan": "33.6"
  },
  "estimasiPopulasi": {
    "totalTanaman": "1,512",
    "note": "Dengan 100 kg bibit G3 (estimasi 15 biji/kg = 1,500 bibit), Anda dapat menanam lahan seluas 725.8 m² dengan jarak tanam 40 cm."
  }
}
```

---

## 3. Fungsi Helper

### A. toNum(x)
Mengkonversi input menjadi angka positif valid.
```javascript
const n = Number(x)
return (Number.isFinite(n) && n > 0) ? n : undefined
```

### B. mergeSeedsPerKg(gen, fromUser, fromDb)
Menggabungkan data seeds_per_kg dari berbagai sumber dengan prioritas:
1. Input user
2. Database
3. Default hardcoded

**Default values:**
- G0: null (tidak perlu seeds_per_kg)
- G2: {min: 15, max: 15}
- G3: {min: 12, max: 18}

### C. mergePrice(gen, userPrice, userUnit, dbMin, dbMax, dbUnit)
Menggabungkan data harga dari berbagai sumber.

**Return modes:**
- `"single"`: Satu harga pasti → `{mode: "single", priceKg: 10000}`
- `"range"`: Range harga → `{mode: "range", minKg: 8000, maxKg: 12000}`
- `"none"`: Tidak ada data harga

**Normalisasi unit:**
```javascript
if (unit === "kuintal") {
    priceKg = price / 100
} else {
    priceKg = price
}
```

---

## 4. Validasi Input

### Validasi Umum
Semua angka harus:
- Finite (bukan NaN atau Infinity)
- Positif (> 0)

### Validasi Khusus

**calculateSeeds:**
- L, W_field, W_ridge, W_furrow, s harus valid

**calculateReverseSeeds:**
- jumlahBibit, jarakTanam, lebarGuludan, lebarParit harus valid
- Untuk G2/G3: `jumlahPerKg` wajib diisi dan > 0

---

## 5. Asumsi dan Catatan

### Asumsi Perhitungan
1. **Guludan minimum:** Sisa lahan minimal 75 cm untuk menambah 1 guludan
2. **Rasio lahan optimal:** 1.5:1 (panjang:lebar) untuk efisiensi
3. **Pembulatan:** Kebutuhan kg selalu dibulatkan ke atas (ceil)
4. **Tanaman per baris:** Dihitung `floor(panjang/jarak) + 1`

### Variabilitas
1. **Ukuran umbi:** G2/G3 memiliki range biji per kg karena variasi ukuran
2. **Harga pasar:** Dapat berfluktuasi, sistem mendukung input manual
3. **Kondisi lahan:** Perhitungan diasumsikan lahan ideal

### Akurasi
- **G0:** Perhitungan eksak (per biji)
- **G2/G3:** Estimasi dengan range (tergantung ukuran umbi)
- **Luas lahan terbalik:** Optimasi matematika, bisa disesuaikan manual

---

## 6. Sumber Data

### Database (seed_parameters table)
```sql
- generation_name      -- G0, G2, G3
- seeds_per_kg_min     -- biji minimum per kg
- seeds_per_kg_max     -- biji maksimum per kg
- price_per_unit_min   -- harga minimum
- price_per_unit_max   -- harga maksimum
- price_unit           -- kg atau kuintal
```

### Default Hardcoded
```javascript
const DEFAULT_SEEDS_PER_KG = {
  G2: { min: 15, max: 15 },
  G3: { min: 12, max: 18 }
}
```

---

**Versi:** 1.0.0  
**Terakhir diperbarui:** 23 November 2025
