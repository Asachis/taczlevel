# TaczLevel — TACZ 枪械等级系统

为 **Timeless and Classics Zero (TACZ)** 添加枪械等级系统。通过战斗获取经验，提升枪械属性。

## 前置需求

- Minecraft **1.20.1**
- **Forge** 47+
- **[Timeless and Classics Zero (TACZ)](https://www.curseforge.com/minecraft/mc-mods/timeless-and-classics-zero)** 1.0+
- **[TaczAttributeAdd (TAA)](https://www.curseforge.com/minecraft/mc-mods/tacz-attribut-add)** 1.0+
- **[KubeJS](https://www.curseforge.com/minecraft/mc-mods/kubejs)** 6.0+
- **[Cloth Config API](https://www.curseforge.com/minecraft/mc-mods/cloth-config)** 11.1+（可选，提供游戏内配置界面）

## 功能

### 升级属性（4 种）

| 属性 | 说明 |
|------|------|
| 装填速度 | 每级减少装弹时间 |
| 后坐力 | 每级降低后坐力 |
| 护甲穿透 | 每级增加穿透百分比 |
| 射速 | 每级增加射速百分比 |

- 每级加成百分比、最大上限均可配置
- 默认每级 +1%/+1%/+1%/+2%，最大等级 100

### 经验获取

- 击杀怪物（动物/中立/普通/精英/Boss/末影龙/凋灵）获得不同经验
- 造成伤害按伤害值 × 倍率获得经验
- 所有经验值可在配置中调整

### 升级台

- **普通枪械升级台** — 生存模式使用：需要消耗激活物品（默认下界之星），单属性升级，50级需要突破界限
- **创造模式枪械升级台** — 无消耗，可同时升级所有属性

### 指令

```
/taczlevel set <option> <level> [player]
/taczlevel upgrade <option> [player]
/taczlevel get [player]
/taczlevel help
```

需要 OP 权限（level 2）。

## 配置

配置文件：`config/taczlevel-common.toml`

安装 Cloth Config API 后可在游戏内通过"Mods → Tacz Level → Config" 直接修改。

## 许可证

[MIT](LICENSE)
