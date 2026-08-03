# GPU Insight AI — Design System & Material 3 Specification

**Author / Maintainer:** Karthik Rajesh Shet ([@karthikrshet](https://github.com/karthikrshet))  
**License:** [Apache-2.0 License](LICENSE)  
**Version:** v1.1.0-spec

---

## 🎨 1. Color System (Obsidian Cyberpunk Dark Palette)

GPU Insight AI uses a custom high-contrast Dark & Material You design palette tailored for dense real-time data visualization and AI infrastructure control:

| Token Name | Hex Code | Purpose |
| :--- | :--- | :--- |
| `ObsidianDark` | `#0B0F19` | Primary Application Background |
| `SurfaceSlate` | `#151C2C` | Card & Elevated Surface Container |
| `CardBorder` | `#222D45` | High-contrast subtle borders |
| `CyberSkyBlue` | `#00E5FF` | Primary Active Brand Accent & Selection |
| `DeepIndigo` | `#3A00E5` | Secondary Brand Accent & Highlights |
| `MintGreen` | `#00E676` | Status: Healthy, Active, ECC Enabled |
| `AlertRed` | `#FF1744` | Status: Critical Temperature / High Fan Speed |
| `WarningAmber` | `#FFC400` | Status: Throttling / High Memory Usage |
| `TextPrimary` | `#F0F4F8` | High-emphasis body and title text |
| `TextSecondary` | `#94A3B8` | Medium-emphasis body and metric labels |
| `TextMuted` | `#64748B` | Disabled / Low-emphasis metadata |

---

## 📐 2. Spacing & Density System

Built strictly on an **8dp grid system**:
- **Micro Spacing (`4.dp`):** Internal label paddings, icon-text gap.
- **Small Spacing (`8.dp`):** Element gaps within cards.
- **Medium Spacing (`16.dp`):** Screen edge padding, outer card gaps.
- **Large Spacing (`24.dp`):** Major section dividers.

---

## 📱 3. Responsive & Adaptive Window Layouts

The application dynamically adjusts navigation and layout containers based on screen dimensions (`WindowSizeClass`):

### Compact Width (< 600dp - Portrait Phones)
- Bottom `NavigationBar` with 5 primary section tabs.
- Full width scrollable cards stacked vertically.

### Medium & Expanded Width (>= 600dp - Landscape Phones, Foldables, Tablets)
- Left-side persistent `NavigationRail` to maximize vertical screen estate.
- Content centered within max container bounds (`Modifier.widthIn(max = 1200.dp)`).
- Dual-pane side-by-side view for GPU list and telemetry breakdown.

---

Copyright © 2026 Karthik Rajesh Shet (@karthikrshet). Released under the Apache-2.0 License.
