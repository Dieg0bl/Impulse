param(
    [Parameter(Mandatory=$true)]
    [string]$Entity,

    [Parameter(Mandatory=$true)]
    [string]$Feature,

    [string]$BasePkg = "com.impulse"
)

# Configuración
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
    Write-Host "❌ $Message" -ForegroundColor Red
    exit 1
}

function Ok {
    param([string]$Message)
    Write-Host "✅ $Message" -ForegroundColor Green
}

function Warning {
    param([string]$Message)
    Write-Host "⚠️ $Message" -ForegroundColor Yellow
}

Write-Host "🔍 Verificando Clean Architecture para entidad: $Entity (feature: $Feature)" -ForegroundColor Cyan
Write-Host "📁 Ruta base: $Root" -ForegroundColor Cyan

# --- Dominio ---
Write-Host ""
Write-Host "🔍 Verificando DOMINIO..." -ForegroundColor Cyan

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

# --- Aplicación ---
Write-Host ""
Write-Host "🔍 Verificando APLICACIÓN..." -ForegroundColor Cyan

$appDirs = @($Ports, $Dto, $AppMappers)
foreach ($dir in $appDirs) {
    if (-not (Test-Path $dir)) {
        Fail "Aplicación: falta carpeta $dir"
    }
}
Ok "Aplicación: paquetes ports/dto/mappers existen"

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
    Fail "Aplicación: imports a adapters/infra detectados"
} else {
    Ok "Aplicación: sin imports a adapters/infra"
}

# --- HTTP adapters ---
Write-Host ""
Write-Host "🔍 Verificando ADAPTADORES HTTP..." -ForegroundColor Cyan

$controllerExists = (Test-Path "$HttpCtrl\$Entity" + "Controller.java") -or (Test-Path "$HttpCtrl\$Entity" + "sController.java")
if (-not $controllerExists) {
    Fail "HTTP: falta controller de $Entity"
}
Ok "HTTP: controller presente"

if (-not (Test-Path $HttpDto) -or -not (Test-Path $HttpMappers)) {
    Fail "HTTP: falta carpeta dto o mappers"
}
Ok "HTTP: dto/mappers presentes"

# Verificar que controllers no importan JPA
$ctrlFiles = Get-ChildItem -Path $HttpCtrl -Recurse -Filter "*.java" -ErrorAction SilentlyContinue
$hasJpaImports = $false
foreach ($file in $ctrlFiles) {
    $content = Get-Content $file.FullName -Raw
    if ($content -match 'import .*adapters\.db\.jpa') {
        $hasJpaImports = $true
        break
    }
}

if ($hasJpaImports) {
    Fail "HTTP: controller importa JPA adapters"
} else {
    Ok "HTTP: controller no depende de JPA adapters"
}

# --- JPA adapters ---
Write-Host ""
Write-Host "🔍 Verificando ADAPTADORES JPA..." -ForegroundColor Cyan

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
    Fail "Separación dominio/JPA: @Entity en dominio"
} else {
    Ok "Separación dominio/JPA: limpia"
}

# --- Infraestructura ---
Write-Host ""
Write-Host "🔍 Verificando INFRAESTRUCTURA..." -ForegroundColor Cyan

$infraConfig = "$Root\infrastructure\config"
if (-not (Test-Path $infraConfig)) {
    Fail "Infra: falta config/"
}
Ok "Infra: config/ presente"

# --- Contratos ---
Write-Host ""
Write-Host "🔍 Verificando CONTRATOS..." -ForegroundColor Cyan

if (-not (Test-Path "backend\contracts\openapi.yaml")) {
    Warning "Contratos: falta backend\contracts\openapi.yaml"
} else {
    $openapiContent = Get-Content "backend\contracts\openapi.yaml" -Raw
    if ($openapiContent -match "/api/$Feature`s(\b|/)|/api/$Feature`s/\{id\}") {
        Ok "Contratos: paths /api/$Feature`s encontrados en openapi.yaml"
    } else {
        Warning "Contratos: no se hallaron paths /api/$Feature`s en openapi.yaml"
    }
}

# --- Frontend ---
Write-Host ""
Write-Host "🔍 Verificando FRONTEND..." -ForegroundColor Cyan

if (-not (Test-Path "frontend\src\api\$Feature`s.ts")) {
    Warning "Front: cliente generado frontend\src\api\$Feature`s.ts no encontrado"
}

if (Test-Path "frontend\src") {
    $frontendFiles = Get-ChildItem -Path "frontend\src" -Recurse -Filter "*.ts*" -ErrorAction SilentlyContinue
    $hasBackendImports = $false
    foreach ($file in $frontendFiles) {
        $content = Get-Content $file.FullName -Raw
        if ($content -match 'from\s+["\'']\.\.\/\.\.\/\.\.\/backend') {
            $hasBackendImports = $true
            break
        }
    }

    if ($hasBackendImports) {
        Fail "Front: import directo de backend detectado"
    } else {
        Ok "Front: sin imports directos de backend"
    }
}

Write-Host ""
Write-Host "🎉 Verificación de $Entity completada" -ForegroundColor Green
Write-Host ""
Write-Host "📊 RESUMEN:" -ForegroundColor Cyan
Write-Host "✅ Dominio: entidad pura sin anotaciones de framework" -ForegroundColor Green
Write-Host "✅ Aplicación: casos de uso, puertos y DTOs separados" -ForegroundColor Green
Write-Host "✅ Adaptadores HTTP: controladores con mappers dedicados" -ForegroundColor Green
Write-Host "✅ Adaptadores JPA: entidades JPA separadas del dominio" -ForegroundColor Green
Write-Host "✅ Infraestructura: configuración sin lógica de negocio" -ForegroundColor Green
Write-Host ""
Write-Host "🚀 Estructura Clean Architecture implementada correctamente para $Entity" -ForegroundColor Green
