package com.toxicbakery.game.dungeon.model.character

import com.toxicbakery.game.dungeon.model.Displayable
import com.toxicbakery.game.dungeon.model.Identifiable
import com.toxicbakery.game.dungeon.model.Living
import com.toxicbakery.game.dungeon.model.Locatable
import com.toxicbakery.game.dungeon.model.Named

interface Npc : Identifiable, Displayable, Named, Locatable, Living
