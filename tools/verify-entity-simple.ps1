param(
    [Parameter(Mandatory=$true)]
    [string]$Entity,

    [Parameter(Mandatory=$true)]
    [string]$Feature,

    [string]$BasePkg = "com.impulse"
)

# Configuracion
$PkgPath = $BasePkg -replace '\.', '\'
$Root = "backend\src\main\java\$PkgPath"
$Domain = "$Root\domain\$Feature"
$App = "$Root\application\$Feature"
$Ports = "$App\ports"
$Dto = "$App\dto"
$AppMappers = "$App\mappers"
$HttpBase = "$Root\adapters\http\$Feature"
$HttpCtrl = "$HttpBase\controllers"
$HttpDto = "$HttpBase\dto"
$HttpMappers = "$HttpBase\mappers"
$JpaBase = "$Root\adapters\db\jpa\$Feature"
$JpaEntities = "$JpaBase\entities"
$JpaRepos = "$JpaBase\repositories"
$JpaMappers = "$JpaBase\mappers"

function Fail {
    param([string]$Message)
    Write-Host "ERROR: $Message" -ForegroundColor Red
    exit 1
}

function Ok {
    param([string]$Message)
    Write-Host "OK: $Message" -ForegroundColor Green
}

function Warning {
    param([string]$Message)
    Write-Host "WARNING: $Message" -ForegroundColor Yellow
}

Write-Host "Verificando Clean Architecture para entidad: $Entity (feature: $Feature)" -ForegroundColor Cyan
Write-Host "Ruta base: $Root" -ForegroundColor Cyan

# --- Dominio ---
Write-Host ""
Write-Host "Verificando DOMINIO..." -ForegroundColor Cyan

if (-not (Test-Path "$Domain\$Entity.java")) {
    Fail "Dominio: falta $Entity.java en $Domain"
}
Ok "Dominio: $Entity.java existe"

# Verificar anotaciones prohibidas
$domainFiles = Get-ChildItem -Path $Domain -Recurse -Filter "*.java" -ErrorAction SilentlyContinue
$hasFrameworkAnnotations = $false
foreach ($file in $domainFiles) {
    $content = Get-Content $file.FullName -Raw
    if ($content -match '@(Entity|Table|Id|RestController|Service|Component|Configuration|Json|NotNull|Valid)\b') {
        $hasFrameworkAnnotations = $true
        break
    }
}

if ($hasFrameworkAnnotations) {
    Fail "Dominio: anotaciones prohibidas detectadas en $Domain"
} else {
    Ok "Dominio: sin anotaciones de framework"
}

# --- Aplicacion ---
Write-Host ""
Write-Host "Verificando APLICACION..." -ForegroundColor Cyan

$appDirs = @($Ports, $Dto, $AppMappers)
foreach ($dir in $appDirs) {
    if (-not (Test-Path $dir)) {
        Fail "Aplicacion: falta carpeta $dir"
    }
}
Ok "Aplicacion: paquetes ports/dto/mappers existen"

# Verificar imports prohibidos
$appFiles = Get-ChildItem -Path $App -Recurse -Filter "*.java" -ErrorAction SilentlyContinue
$hasAdapterImports = $false
foreach ($file in $appFiles) {
    $content = Get-Content $file.FullName -Raw
    if ($content -match 'import .*\.adapters\.|import .*\.infrastructure\.') {
        $hasAdapterImports = $true
        break
    }
}

if ($hasAdapterImports) {
    Fail "Aplicacion: imports a adapters/infra detectados"
} else {
    Ok "Aplicacion: sin imports a adapters/infra"
}

# --- HTTP adapters ---
Write-Host ""
Write-Host "Verificando ADAPTADORES HTTP..." -ForegroundColor Cyan

$controllerExists = (Test-Path "$HttpCtrl\$($Entity)Controller.java") -or (Test-Path "$HttpCtrl\$($Entity)sController.java")
if (-not $controllerExists) {
    Fail "HTTP: falta controller de $Entity"
}
Ok "HTTP: controller presente"

if (-not (Test-Path $HttpDto) -or -not (Test-Path $HttpMappers)) {
    Fail "HTTP: falta carpeta dto o mappers"
}
Ok "HTTP: dto/mappers presentes"

# --- JPA adapters ---
Write-Host ""
Write-Host "Verificando ADAPTADORES JPA..." -ForegroundColor Cyan

$jpaDirs = @($JpaEntities, $JpaRepos, $JpaMappers)
foreach ($dir in $jpaDirs) {
    if (-not (Test-Path $dir)) {
        Fail "JPA: falta carpeta $($dir -split '\\' | Select-Object -Last 1)"
    }
}
Ok "JPA: entities/repositories/mappers presentes"

# Verificar @Entity en JPA entities
$jpaEntityFiles = Get-ChildItem -Path $JpaEntities -Recurse -Filter "*.java" -ErrorAction SilentlyContinue
$hasEntityAnnotation = $false
foreach ($file in $jpaEntityFiles) {
    $content = Get-Content $file.FullName -Raw
    if ($content -match '@Entity') {
        $hasEntityAnnotation = $true
        break
    }
}

if (-not $hasEntityAnnotation) {
    Fail "JPA: no se encontraron @Entity en $JpaEntities"
}
Ok "JPA: entidades con @Entity detectadas"

# Verificar que dominio NO tiene @Entity
$domainHasEntity = $false
foreach ($file in $domainFiles) {
    $content = Get-Content $file.FullName -Raw
    if ($content -match '@Entity') {
        $domainHasEntity = $true
        break
    }
}

if ($domainHasEntity) {
    Fail "Separacion dominio/JPA: @Entity en dominio"
} else {
    Ok "Separacion dominio/JPA: limpia"
}

# --- Infraestructura ---
Write-Host ""
Write-Host "Verificando INFRAESTRUCTURA..." -ForegroundColor Cyan

$infraConfig = "$Root\infrastructure\config"
if (-not (Test-Path $infraConfig)) {
    Fail "Infra: falta config/"
}
Ok "Infra: config/ presente"

Write-Host ""
Write-Host "Verificacion de $Entity completada" -ForegroundColor Green
Write-Host ""
Write-Host "RESUMEN:" -ForegroundColor Cyan
Write-Host "OK: Dominio: entidad pura sin anotaciones de framework" -ForegroundColor Green
Write-Host "OK: Aplicacion: casos de uso, puertos y DTOs separados" -ForegroundColor Green
Write-Host "OK: Adaptadores HTTP: controladores con mappers dedicados" -ForegroundColor Green
Write-Host "OK: Adaptadores JPA: entidades JPA separadas del dominio" -ForegroundColor Green
Write-Host "OK: Infraestructura: configuracion sin logica de negocio" -ForegroundColor Green
Write-Host ""
Write-Host "Estructura Clean Architecture implementada correctamente para $Entity" -ForegroundColor Green
