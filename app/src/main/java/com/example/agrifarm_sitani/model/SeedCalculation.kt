package com.example.agrifarm_sitani.model

data class SeedCalculation(
    val ringkasanLahan: RingkasanLahan,
    val kebutuhanTanam: KebutuhanTanam,
    val kebutuhanBibit: KebutuhanBibit,
    val estimasiBiaya: EstimasiBiaya? = null
)

data class RingkasanLahan(
    val lebarUnitTanam: Double,
    val jumlahGuludan: Int,
    val panjangTanamPerGuludan: Double
)

data class KebutuhanTanam(
    val jumlahTanamanPerGuludan: Int,
    val totalPopulasiTanaman: Int
)

data class KebutuhanBibit(
    val estimasi: String,
    val unit: String,
    val rangeKg: RangeKg? = null,
    val totalBiji: Int? = null,
    val note: String
)

data class RangeKg(
    val kgMin: Int,
    val kgEst: Int,
    val kgMax: Int
)

data class EstimasiBiaya(
    val total: String
)

data class ReverseCalculation(
    val ringkasan: RingkasanReverse,
    val estimasiPopulasi: EstimasiPopulasi
)

data class RingkasanReverse(
    val estimasiLuasM2: Double,
    val jumlahGuludan: Int,
    val panjangPerGuludan: Double
)

data class EstimasiPopulasi(
    val totalTanaman: Int,
    val note: String
)

enum class GenerasiBibit(val displayName: String) {
    G0("G0"),
    G2("G2"),
    G3("G3")
}
