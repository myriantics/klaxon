# Reworks
- ### Entity Weight
  - No longer Attribute Modifier-based.
  - Now is a simple boolean yes/no. An entity is either heavy or light.
  - Read the Wiki page for more info.
- ### Blast Processing Recipes
  - Now supports Chance Outputs!
- ### Steel Wrench
  - Now supports Steel & Crude Steel Doors & Trapdoors.
  - "Wrenchable" interface now exists which allows addition of wrench support to more blocks more easily. Currently only used for the above mentioned and the Hallnox Bulb.
- ### Steel Cable Shears
  - Now acts like Shears when processing loot tables.
- ### GameRules
  - **shouldBlastProcessorExplosionsModifyWorld** GameRule now uses the klaxon.\[NAME] naming convention.
- ### Hammer Walljumping
  - Is no longer fully disabled by Heavy Equipment.
- ### Tool Usage Recipes
  - Have been re-hardcoded for better UX.
# Features
## Content
- ### Hallnox Fungus / Blockset
  - An inert, engineered derivative of Warped & Crimson Fungus that can thrive in any environment, from the deepest depths of the ocean to the farthest reaches of space.
  - While it does not directly retain the special properties of its predecessors, some form of magic seems to linger within.
  - **Hallnox Woodset**
    - Due to being a derivative of Nether Fungus, the Hallnox Woodset does not have a boat. However, it is fireproof.
  - **Hallnox Pod**
    - The stylish sapling-ish-thing of Hallnox Fungus. It's an instance of SaplingBlock, so that kinda counts.
    - Currently obtainable from Sniffers, although that might change in the future. Wink wink.
    - Can grow in all directions. Upright, sideways, and downright have different tree shapes.
    - Falls when struck with a projectile.
    - Growth is disabled automatically when attached to Hallnox Wart, Hyphae, or Stems. Can also be disabled manually via Shears.
    - Can be used on any quality of Steel Casing to create a Nether Reactor Core.
  - **Hallnox Bulb**
    - Stylish fusion of Hallnox, Glass, and Steel.
    - A light fixture sure to wow any houseguests.
    - Automatically connects to other Bulbs when placing. You'll figure out how it works.
    - Connections can be toggled with the Steel Wrench.
- ### Grapple Winch & Steel Grapple Claw
  - **Grapple Winch**
    - The latest & greatest pseudolegendary item offered by KLAXON.
    - Hybrid self-movement, other-movement, mining, and veinmining tool.
      - If you want more info, read the wiki page.
    - Requires Steel Grapple Claws to function.
  - **Steel Grapple Claw**
    - Ammunition for the Grapple Winch, although it can exist on its own.
    - Instantly destroys any valid blocks in its path. By default, this includes Cleaver-Instabreakable blocks among other things.
    - Has durability and can stack to 16. 
      - I know that sounds cursed, but it's fine because you can only damage them one at a time in the Grapple Winch.
      - If you have too many, you can always just craft them together
- ### Nether Reactor Core (and the Crude Variant)
  - Detonating an explosive that produces a vanilla explosion within either of these blocks will trigger a Nether Reaction Explosion.
  - The main difference between the two Nether Reactor Cores is that the Steel one leaves the casing behind after activation, while the Crude one is destroyed.
- ### Wires & Wire Spool Blocks
  - Prepping for adding machines. It's coming soon, trust me haha.
  - Right now the Steel Wire Spool is used to make the Grapple Winch.
  - There's also full oxidation stages for the Copper Wire Spools, which look pretty cool.
## Recipe Types
- ### Nether Reaction
  - Hijacks an explosion that goes off within a Nether Reactor Core, then turns all nearby valid blocks into their Nether counterparts.
  - Put in as a progression failsafe against Nether biome mods - Warped & Crimson Fungi will be vital to progression in the future.
- ### Manual Item Application
  - TOTALLY not ripping off Create. Definitely not.
  - Allows you to use an item on a block to transform it into another block. 100% original.
  - Is currently only used to make Nether Reactor Cores.
  - Has a unique feature in that all KLAXON Manual Item Application recipes can be performed by Dispensers.
## GameRules
 - ### klaxon.dispensersPerformItemInteractionRecipes
   - Allows server admins and pack devs to decide if they want to have Dispensers be able to perform Manual Item Interaction recipes or not.
 - ### klaxon.grappleClawVeinmineRadius
   - Defines the radius that the Grapple Claw checks when veinmining. Values can range from 16 to 64.
## Advancements
- Advancement Page renamed from "KLAXON - Prelude" to "KLAXON - Initialization". Mainly because I felt that was ripping off ULTRAKILL a bit too much.
- ### Uses 96 Batteries
  - Obtain a Hallnox Pod.
- ### THE NETHER.
  - Activate a Nether Reactor Core by detonating an exposion within the block.
- ### Smash And Grab
  - Veinmine Glowstone by retracting an attached Grapple Claw with the Grapple Winch
## Splashes
- ### OH MY BLOCKS!
  - Sourced from a Minecraft manga book that my friends and I picked up. Thought it was funny enough to add.
- ### THIS IS MY CRAFT!
  - Same as above. :)
# Compat
### Jade
- Added Jade tooltip for Hallnox Pod growth status. Indicates whether growth is inhibited by supporting block or has been disabled with Shears.
- Steel Cleaver is now registered under the Axe category - no longer will the icon show up when highlighting any axe-mineable block.
### EMI
- Added separate recipe categories for Hammering & Wirecutting
- Added compat for Nether Reaction recipes
- Added compat for Manual Item Application Recipes