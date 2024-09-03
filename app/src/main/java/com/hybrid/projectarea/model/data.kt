package com.hybrid.projectarea.model

import androidx.camera.core.processing.SurfaceProcessorNode.In
import com.google.gson.annotations.SerializedName
import java.util.Date

data class LoginRequest(
    val dni: String,
    val password: String
)

data class LoginResponse(
    val id: String,
    val token: String
)

data class UsersResponse(
    val id: String,
    val name: String,
    val dni: String,
    val email: String
)

data class PhotoRequest(
    val id: String,
    val description: String,
    val photo: String,
    val latitude: String? = null,
    val longitude: String? = null,
)

data class ElementPreProjectRecyclerView(
    @SerializedName("preproject_id") val id: String,
    val code: String,
    val description: String,
    val date: String,
    val observation: String,
)

data class ProjectRecycler(
    val id: String,
    val code: String,
    val description: String,
)

data class ProjectFind(
    val id: String,
    val code: String,
    val description: String,
)

data class PreprojectTitle(
    val id: String,
    val type: String,
    val preproject_codes: List<CodePhotoPreProject>
)

data class CodePhotoPreProject(
    val id: String,
    val code: Code,
    val status: String?,
    val replaceable_status: String,
    val rejected_quantity: Int
)

data class Code(
    val code: String
)

data class CodePhotoDescription(
    val id: String,
    val codePreproject: String,
    val code: String,
    val description: String,
    val status: String,
)

data class Photo(
    val image: String,
    val observation: String,
    val state: String,
)

data class ProjectHuawei(
    val id: String,
    val site: String,
    val elaborated: String,
    val code: String,
    val name: String,
    val address: String,
    val reference: String,
    val access: String
)

data class FormStoreProjectHuawei(
    val site: String,
    val elaborated: String,
    val code: String,
    val name: String,
    val address: String,
    val reference: String,
    val access: String
)


data class FormDataACHuawei(
    val power: String,
    val concessionaire: String,
    val supply: String,
    val type: String,
    val caliber: String,
    val fuses: String,
    val calibertg: String,
    val itm: String,
    val powere: String,
    val brand: String,
    val tankCapacity: String,
    val typee: String,
    val tableTransfer: String,
    val capacity: String,
    val fijacion: String,
    val typet: String,
    val section: String,
    val itmMajor: String,
    val rs: String,
    val rt: String,
    val st: String,
    val r: String,
    val s: String,
    val t: String
)

data class NameRectifiers(
    val id: String,
    val brand:String,
)

data class FormProcessManuals(
    val root: String,
    val path: String
)

data class FolderArchiveResponse(
    val folders_archives: List<GetProcessManuals>,
    val currentPath: String,
    val previousPath: String
)

data class GetProcessManuals(
    val name: String,
    val type: String,
    val path: String,
    val size: String?
)

data class Download(
    val path: String
)

data class checkListTools(
    val reason: String,
    val additionalEmployees: String,
    val zone: String,
    val carabiner: String,
    val wireStripper: String,
    val crimper: String,
    val terminalCrimper: String,
    val files: String,
    val allenKeys: String,
    val readlineKit: String,
    val impactWrench: String,
    val dielectricTools: String,
    val cuttingTools: String,
    val forceps: String,
    val straightWrench: String,
    val frenchWrench: String,
    val saw: String,
    val silicone: String,
    val pulley: String,
    val tapeMeasure: String,
    val sling: String,
    val kit: String,
    val drillBits: String,
    val punch: String,
    val extractor: String,
    val wrenchSet: String,
    val braveDices: String,
    val cutter: String,
    val hammer: String,
    val largeToolBag: String,
    val mediumToolBag: String,
    val fallProtectionCar: String,
    val harness: String,
    val pressureWasher: String,
    val blower: String,
    val megommeter: String,
    val earthTester: String,
    val perimeterMeter: String,
    val manometer: String,
    val pyrometer: String,
    val laptop: String,
    val drill: String,
    val compass: String,
    val inclinometer: String,
    val flashlight: String,
    val powerMeter: String,
    val glueGun: String,
    val solderingGun: String,
    val stepLadder: String,
    val sprayer: String,
    val rj45Connector: String,
    val networkConsole: String,
    val networkAdapter: String,
    val hotStick: String,
    val rope75: String,
    val ladder: String,
    val extensionCord: String,
    val longCable: String,
    val padlock: String,
    val chains: String,
    val hose: String,
    val corporatePhone: String,
    val observation: String,
    val badTools: String,
    val goodTools: String
)

data class checkListMobile(
    val reason:String,
    val additionalEmployees: String,
    val zone: String,
    val km: String,
    val plate: String,
    val circulation: String,
    val technique: String,
    val soat: String,

    val hornState: String,
    val brakesState: String,
    val headlightsState: String,
    val intermitentlightState: String,
    val indicatorsState: String,
    val mirrorsState: String,
    val tiresState: String,
    val bumpersState: String,
    val temperatureGauge: String,
    val oilGauge: String,
    val fuelGauge: String,
    val vehicleCleanliness: String,
    val doorsState: String,
    val windshieldState: String,
    val engineState: String,
    val batteryState: String,

    val extinguisher: String,
    val firstAidKit: String,
    val cones: String,
    val jack: String,
    val spareTire: String,
    val towCable: String,
    val batteryCable: String,
    val reflector: String,
    val emergencyKit: String,
    val alarm: String,
    val chocks: String,
    val ladderHolder: String,
    val sidePlate: String,

    val observation: String,
    val front: String,
    val leftSide: String,
    val rightSide: String,
    val interior: String,
    val rearLeftTire: String,
    val rearRightTire: String,
    val frontRightTire: String,
    val frontLeftTire: String,
)

data class checklistDay(
    @SerializedName("personal_2") val personal2: String?,
    @SerializedName("zone") val zone: String,
    @SerializedName("power_meter") val powerMeter: String,
    @SerializedName("ammeter_clamp") val ammeterClamp: String,
    @SerializedName("cutting_pliers") val cuttingPliers: String,
    @SerializedName("nose_pliers") val nosePliers: String,
    @SerializedName("universal_pliers") val universalPliers: String,
    @SerializedName("tape") val tape: String,
    @SerializedName("cutter") val cutter: String,
    @SerializedName("knob_driver") val knobDriver: String,
    @SerializedName("screwdriver_set") val screwdriverSet: String,
    @SerializedName("allenkeys_set") val allenkeysSet: String,
    @SerializedName("thor_screwboard") val thorScrewboard: String,
    @SerializedName("helmet_flashlight") val helmetFlashlight: String,
    @SerializedName("freanch_key") val freanchKey: String,
    @SerializedName("pyrometer") val pyrometer: String,
    @SerializedName("laptop") val laptop: String,
    @SerializedName("console_cables") val consoleCables: String,
    @SerializedName("network_adapter") val networkAdapter: String,
    @SerializedName("observations") val observations: String?
)


data class checklistEpps(
    @SerializedName("helmet") val helmet: String,
    @SerializedName("chin_strap") val chinStrap: String,
    @SerializedName("windbreaker") val windbreaker: String,
    @SerializedName("vest") val vest: String,
    @SerializedName("safety_shoes") val safetyShoes: String,
    @SerializedName("tshirt_ls") val tshirtLs: String,
    @SerializedName("shirt_ls") val shirtLs: String,
    @SerializedName("jeans") val jeans: String,
    @SerializedName("coveralls") val coveralls: String,
    @SerializedName("jacket") val jacket: String,
    @SerializedName("dark_glasses") val darkGlasses: String,
    @SerializedName("clear_glasses") val clearGlasses: String,
    @SerializedName("overglasses") val overglasses: String,
    @SerializedName("dust_mask") val dustMask: String,
    @SerializedName("earplugs") val earplugs: String,
    @SerializedName("latex_oil_gloves") val latexOilGloves: String,
    @SerializedName("nitrile_oil_gloves") val nitrileOilGloves: String,
    @SerializedName("split_leather_gloves") val splitLeatherGloves: String,
    @SerializedName("precision_gloves") val precisionGloves: String,
    @SerializedName("cut_resistant_gloves") val cutResistantGloves: String,
    @SerializedName("double_lanyard") val doubleLanyard: String,
    @SerializedName("harness") val harness: String,
    @SerializedName("positioning_lanyard") val positioningLanyard: String,
    @SerializedName("carabiners") val carabiners: String,
    @SerializedName("ascenders") val ascenders: String,
    @SerializedName("sunscreen") val sunscreen: String,
    @SerializedName("ccip") val ccip: String,
    @SerializedName("claro") val claro: String,
    @SerializedName("vericom") val vericom: String
)

data class ChecklistHistory(
    val type :String,
    val created_at: String,
)

data class ExpenseForm(
    val zone: String,
    val expense_type: String,
    val type_doc: String,
    val ruc: String,
    val doc_number: String,
    val doc_date: String,
    val amount: String,
    val description: String,
    val photo: String,
    val project_id: String,
)

data class ExpenseHistory(
    val zone :String,
    val expense_type: String,
    val amount :String,
)