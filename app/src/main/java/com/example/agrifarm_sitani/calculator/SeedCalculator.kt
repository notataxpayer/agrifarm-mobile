package com.example.agrifarm_sitani.calculator

import com.example.agrifarm_sitani.model.*
import kotlin.math.*

object SeedCalculator {

    private val DEFAULT_SEEDS_PER_KG = mapOf(
        GenerasiBibit.G2 to Pair(15, 15),
        GenerasiBibit.G3 to Pair(12, 18)
    )

    fun calculateSeeds(
        panjangLahan: Double,
        lebarLahan: Double,
        lebarGuludan: Double = 80.0,
        lebarParit: Double,
        jarakTanam: Double,
        generasiBibit: GenerasiBibit,
        jumlahBibitPerKg: Int? = null,
        estimasiHarga: Double? = null,
        estimasiHargaUnit: String? = null
    ): SeedCalculation? {

        if (panjangLahan <= 0 || lebarLahan <= 0 || lebarGuludan <= 0 ||
            lebarParit <= 0 || jarakTanam <= 0) {
            return null
        }

        val wRidge = lebarGuludan / 100
        val wFurrow = lebarParit / 100
        val spacing = jarakTanam / 100

        val unitWidth = wRidge + wFurrow
        val jumlahGuludan = floor(lebarLahan / unitWidth).toInt()
        val sisa = lebarLahan - (jumlahGuludan * unitWidth)
        val ridgeMin = 0.75
        val finalGuludan = if (sisa >= ridgeMin) jumlahGuludan + 1 else jumlahGuludan

        val tanamanPerGuludan = floor(panjangLahan / spacing).toInt() + 1
        val totalPopulasi = finalGuludan * tanamanPerGuludan

        val ringkasanLahan = RingkasanLahan(
            lebarUnitTanam = unitWidth,
            jumlahGuludan = finalGuludan,
            panjangTanamPerGuludan = panjangLahan
        )

        val kebutuhanTanam = KebutuhanTanam(
            jumlahTanamanPerGuludan = tanamanPerGuludan,
            totalPopulasiTanaman = totalPopulasi
        )

        val kebutuhanBibit = when (generasiBibit) {
            GenerasiBibit.G0 -> {
                KebutuhanBibit(
                    estimasi = "$totalPopulasi biji",
                    unit = "biji",
                    totalBiji = totalPopulasi,
                    note = "Kebutuhan bibit G0 dihitung per biji."
                )
            }
            else -> {
                val seedsPerKg = jumlahBibitPerKg?.let { Pair(it, it) }
                    ?: DEFAULT_SEEDS_PER_KG[generasiBibit]!!

                val kgMin = ceil(totalPopulasi.toDouble() / seedsPerKg.second).toInt()
                val kgMax = ceil(totalPopulasi.toDouble() / seedsPerKg.first).toInt()
                val kgEst = ceil((kgMin + kgMax) / 2.0).toInt()

                KebutuhanBibit(
                    estimasi = "$kgEst kg (${String.format("%.2f", kgEst / 100.0)} kuintal)",
                    unit = "kg",
                    rangeKg = RangeKg(kgMin, kgEst, kgMax),
                    note = "Angka perkiraan. Kebutuhan bisa lebih sedikit/lebih banyak tergantung ukuran umbi (biji/kg)."
                )
            }
        }

        val estimasiBiaya = if (estimasiHarga != null && estimasiHargaUnit != null &&
            kebutuhanBibit.rangeKg != null) {
            val priceKg = if (estimasiHargaUnit == "kuintal") estimasiHarga / 100 else estimasiHarga
            val biayaEst = (priceKg * kebutuhanBibit.rangeKg.kgEst).toLong()
            EstimasiBiaya(
                total = "Rp ${formatRupiah(biayaEst)}"
            )
        } else null

        return SeedCalculation(ringkasanLahan, kebutuhanTanam, kebutuhanBibit, estimasiBiaya)
    }

    fun calculateReverseSeeds(
        jumlahBibit: Double,
        jarakTanam: Double,
        lebarGuludan: Double = 80.0,
        lebarParit: Double,
        generasiBibit: GenerasiBibit,
        jumlahPerKg: Int? = null
    ): ReverseCalculation? {

        if (jumlahBibit <= 0 || jarakTanam <= 0 || lebarGuludan <= 0 || lebarParit <= 0) {
            return null
        }

        if (generasiBibit != GenerasiBibit.G0 && jumlahPerKg == null) {
            return null
        }

        val spacing = jarakTanam / 100
        val wRidge = lebarGuludan / 100
        val wFurrow = lebarParit / 100

        val totalTanaman = if (generasiBibit == GenerasiBibit.G0) {
            jumlahBibit.toInt()
        } else {
            (jumlahBibit * jumlahPerKg!!).toInt()
        }

        val lebarUnitTanam = wRidge + wFurrow
        val targetRasio = 1.5
        var jumlahGuludan = round(sqrt(totalTanaman * spacing / (targetRasio * lebarUnitTanam))).toInt()
        if (jumlahGuludan < 1) jumlahGuludan = 1

        val tanamanPerGuludan = ceil(totalTanaman.toDouble() / jumlahGuludan).toInt()
        val panjangPerGuludan = tanamanPerGuludan * spacing
        val lebarLahan = jumlahGuludan * lebarUnitTanam
        val estimasiLuasM2 = panjangPerGuludan * lebarLahan
        val tanamanAktual = jumlahGuludan * tanamanPerGuludan

        val note = when (generasiBibit) {
            GenerasiBibit.G0 -> "Dengan ${jumlahBibit.toInt()} bibit G0, Anda dapat menanam lahan seluas ${String.format("%.1f", estimasiLuasM2)} m² dengan jarak tanam ${jarakTanam.toInt()} cm."
            else -> "Dengan ${jumlahBibit.toInt()} kg bibit ${generasiBibit.displayName} (estimasi $jumlahPerKg biji/kg = $totalTanaman bibit), Anda dapat menanam lahan seluas ${String.format("%.1f", estimasiLuasM2)} m² dengan jarak tanam ${jarakTanam.toInt()} cm."
        }

        return ReverseCalculation(
            ringkasan = RingkasanReverse(
                estimasiLuasM2 = estimasiLuasM2,
                jumlahGuludan = jumlahGuludan,
                panjangPerGuludan = panjangPerGuludan
            ),
            estimasiPopulasi = EstimasiPopulasi(
                totalTanaman = tanamanAktual,
                note = note
            )
        )
    }

    private fun formatRupiah(angka: Long): String {
        return angka.toString().reversed().chunked(3).joinToString(".").reversed()
    }
}
