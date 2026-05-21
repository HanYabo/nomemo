/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.app.Activity
 *  android.content.BroadcastReceiver
 *  android.content.Context
 *  android.content.ContextWrapper
 *  android.content.Intent
 *  android.content.IntentFilter
 *  android.os.Bundle
 *  android.view.Window
 *  android.widget.Toast
 *  androidx.activity.ComponentActivity
 *  androidx.activity.compose.BackHandlerKt
 *  androidx.activity.compose.ComponentActivityKt
 *  androidx.activity.result.ActivityResult
 *  androidx.activity.result.ActivityResultLauncher
 *  androidx.activity.result.contract.ActivityResultContract
 *  androidx.activity.result.contract.ActivityResultContracts$StartActivityForResult
 *  androidx.compose.animation.AnimatedVisibilityKt
 *  androidx.compose.animation.AnimatedVisibilityScope
 *  androidx.compose.animation.EnterExitTransitionKt
 *  androidx.compose.animation.EnterTransition
 *  androidx.compose.animation.ExitTransition
 *  androidx.compose.animation.core.AnimateAsStateKt
 *  androidx.compose.animation.core.AnimationSpec
 *  androidx.compose.animation.core.AnimationSpecKt
 *  androidx.compose.animation.core.Easing
 *  androidx.compose.animation.core.EasingKt
 *  androidx.compose.animation.core.FiniteAnimationSpec
 *  androidx.compose.foundation.BackgroundKt
 *  androidx.compose.foundation.BorderKt
 *  androidx.compose.foundation.BorderStroke
 *  androidx.compose.foundation.BorderStrokeKt
 *  androidx.compose.foundation.ClickableKt
 *  androidx.compose.foundation.DarkThemeKt
 *  androidx.compose.foundation.Indication
 *  androidx.compose.foundation.OverscrollEffect
 *  androidx.compose.foundation.ScrollKt
 *  androidx.compose.foundation.ScrollState
 *  androidx.compose.foundation.gestures.FlingBehavior
 *  androidx.compose.foundation.interaction.InteractionSource
 *  androidx.compose.foundation.interaction.InteractionSourceKt
 *  androidx.compose.foundation.interaction.MutableInteractionSource
 *  androidx.compose.foundation.interaction.PressInteractionKt
 *  androidx.compose.foundation.layout.Arrangement
 *  androidx.compose.foundation.layout.Arrangement$Horizontal
 *  androidx.compose.foundation.layout.Arrangement$HorizontalOrVertical
 *  androidx.compose.foundation.layout.Arrangement$Vertical
 *  androidx.compose.foundation.layout.BoxKt
 *  androidx.compose.foundation.layout.BoxScope
 *  androidx.compose.foundation.layout.BoxScopeInstance
 *  androidx.compose.foundation.layout.BoxWithConstraintsKt
 *  androidx.compose.foundation.layout.BoxWithConstraintsScope
 *  androidx.compose.foundation.layout.ColumnKt
 *  androidx.compose.foundation.layout.ColumnScope
 *  androidx.compose.foundation.layout.ColumnScopeInstance
 *  androidx.compose.foundation.layout.OffsetKt
 *  androidx.compose.foundation.layout.PaddingKt
 *  androidx.compose.foundation.layout.PaddingValues
 *  androidx.compose.foundation.layout.RowKt
 *  androidx.compose.foundation.layout.RowScope
 *  androidx.compose.foundation.layout.RowScopeInstance
 *  androidx.compose.foundation.layout.SizeKt
 *  androidx.compose.foundation.layout.SpacerKt
 *  androidx.compose.foundation.layout.WindowInsetsPadding_androidKt
 *  androidx.compose.foundation.lazy.LazyDslKt
 *  androidx.compose.foundation.lazy.LazyItemScope
 *  androidx.compose.foundation.lazy.LazyListScope
 *  androidx.compose.foundation.lazy.LazyListState
 *  androidx.compose.foundation.lazy.LazyListStateKt
 *  androidx.compose.foundation.text.BasicTextFieldKt
 *  androidx.compose.material3.CardColors
 *  androidx.compose.material3.CardDefaults
 *  androidx.compose.material3.CardElevation
 *  androidx.compose.material3.CardKt
 *  androidx.compose.material3.IconKt
 *  androidx.compose.material3.TextKt
 *  androidx.compose.runtime.Applier
 *  androidx.compose.runtime.Composable
 *  androidx.compose.runtime.ComposableInferredTarget
 *  androidx.compose.runtime.ComposableTarget
 *  androidx.compose.runtime.ComposablesKt
 *  androidx.compose.runtime.Composer
 *  androidx.compose.runtime.ComposerKt
 *  androidx.compose.runtime.CompositionLocal
 *  androidx.compose.runtime.CompositionLocalMap
 *  androidx.compose.runtime.DisposableEffectResult
 *  androidx.compose.runtime.DisposableEffectScope
 *  androidx.compose.runtime.EffectsKt
 *  androidx.compose.runtime.IntState
 *  androidx.compose.runtime.MutableIntState
 *  androidx.compose.runtime.MutableState
 *  androidx.compose.runtime.RecomposeScopeImplKt
 *  androidx.compose.runtime.ScopeUpdateScope
 *  androidx.compose.runtime.SnapshotIntStateKt
 *  androidx.compose.runtime.SnapshotStateKt
 *  androidx.compose.runtime.State
 *  androidx.compose.runtime.Updater
 *  androidx.compose.runtime.internal.ComposableLambdaKt
 *  androidx.compose.runtime.internal.StabilityInferred
 *  androidx.compose.ui.Alignment
 *  androidx.compose.ui.Alignment$Horizontal
 *  androidx.compose.ui.Alignment$Vertical
 *  androidx.compose.ui.ComposedModifierKt
 *  androidx.compose.ui.Modifier
 *  androidx.compose.ui.ZIndexModifierKt
 *  androidx.compose.ui.draw.AlphaKt
 *  androidx.compose.ui.draw.CacheDrawScope
 *  androidx.compose.ui.draw.ClipKt
 *  androidx.compose.ui.draw.DrawModifierKt
 *  androidx.compose.ui.draw.DrawResult
 *  androidx.compose.ui.draw.ShadowKt
 *  androidx.compose.ui.geometry.Offset
 *  androidx.compose.ui.graphics.Brush
 *  androidx.compose.ui.graphics.Brush$Companion
 *  androidx.compose.ui.graphics.Color
 *  androidx.compose.ui.graphics.ColorKt
 *  androidx.compose.ui.graphics.GraphicsLayerModifierKt
 *  androidx.compose.ui.graphics.GraphicsLayerScope
 *  androidx.compose.ui.graphics.Shadow
 *  androidx.compose.ui.graphics.Shape
 *  androidx.compose.ui.graphics.drawscope.ContentDrawScope
 *  androidx.compose.ui.graphics.drawscope.DrawScope
 *  androidx.compose.ui.graphics.painter.Painter
 *  androidx.compose.ui.layout.MeasurePolicy
 *  androidx.compose.ui.node.ComposeUiNode
 *  androidx.compose.ui.platform.AndroidCompositionLocals_androidKt
 *  androidx.compose.ui.platform.CompositionLocalsKt
 *  androidx.compose.ui.res.PainterResources_androidKt
 *  androidx.compose.ui.res.StringResources_androidKt
 *  androidx.compose.ui.semantics.Role
 *  androidx.compose.ui.text.TextRangeKt
 *  androidx.compose.ui.text.TextStyle
 *  androidx.compose.ui.text.font.FontWeight
 *  androidx.compose.ui.text.input.TextFieldValue
 *  androidx.compose.ui.text.style.TextOverflow
 *  androidx.compose.ui.unit.Density
 *  androidx.compose.ui.unit.Dp
 *  androidx.compose.ui.unit.DpKt
 *  androidx.compose.ui.unit.IntRect
 *  androidx.compose.ui.unit.TextUnitKt
 *  androidx.core.content.ContextCompat
 *  androidx.lifecycle.LifecycleOwner
 *  androidx.lifecycle.LifecycleOwnerKt
 *  com.han.nomemo.MemoryRecord
 *  com.han.nomemo.MemoryStore
 *  com.han.nomemo.R$anim
 *  com.han.nomemo.R$drawable
 *  com.han.nomemo.R$string
 *  com.kyant.backdrop.Backdrop
 *  com.kyant.backdrop.backdrops.LayerBackdrop
 *  com.kyant.backdrop.backdrops.LayerBackdropKt
 *  com.kyant.backdrop.backdrops.LayerBackdropModifierKt
 *  com.kyant.capsule.ContinuousRoundedRectangle
 *  kotlin.Lazy
 *  kotlin.LazyKt
 *  kotlin.Metadata
 *  kotlin.ResultKt
 *  kotlin.TuplesKt
 *  kotlin.Unit
 *  kotlin.collections.CollectionsKt
 *  kotlin.collections.MapsKt
 *  kotlin.collections.SetsKt
 *  kotlin.coroutines.Continuation
 *  kotlin.coroutines.CoroutineContext
 *  kotlin.coroutines.intrinsics.IntrinsicsKt
 *  kotlin.jvm.functions.Function0
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.functions.Function2
 *  kotlin.jvm.functions.Function3
 *  kotlin.jvm.functions.Function4
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.ranges.RangesKt
 *  kotlin.text.StringsKt
 *  kotlinx.coroutines.BuildersKt
 *  kotlinx.coroutines.CoroutineScope
 *  kotlinx.coroutines.DelayKt
 *  kotlinx.coroutines.Dispatchers
 *  kotlinx.coroutines.Job
 *  kotlinx.coroutines.Job$DefaultImpls
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.han.nomemo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;
import androidx.activity.ComponentActivity;
import androidx.activity.compose.BackHandlerKt;
import androidx.activity.compose.ComponentActivityKt;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.compose.animation.AnimatedVisibilityKt;
import androidx.compose.animation.AnimatedVisibilityScope;
import androidx.compose.animation.EnterExitTransitionKt;
import androidx.compose.animation.EnterTransition;
import androidx.compose.animation.ExitTransition;
import androidx.compose.animation.core.AnimateAsStateKt;
import androidx.compose.animation.core.AnimationSpec;
import androidx.compose.animation.core.AnimationSpecKt;
import androidx.compose.animation.core.Easing;
import androidx.compose.animation.core.EasingKt;
import androidx.compose.animation.core.FiniteAnimationSpec;
import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.BorderKt;
import androidx.compose.foundation.BorderStroke;
import androidx.compose.foundation.BorderStrokeKt;
import androidx.compose.foundation.ClickableKt;
import androidx.compose.foundation.DarkThemeKt;
import androidx.compose.foundation.Indication;
import androidx.compose.foundation.OverscrollEffect;
import androidx.compose.foundation.ScrollKt;
import androidx.compose.foundation.ScrollState;
import androidx.compose.foundation.gestures.FlingBehavior;
import androidx.compose.foundation.interaction.InteractionSource;
import androidx.compose.foundation.interaction.InteractionSourceKt;
import androidx.compose.foundation.interaction.MutableInteractionSource;
import androidx.compose.foundation.interaction.PressInteractionKt;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.BoxKt;
import androidx.compose.foundation.layout.BoxScope;
import androidx.compose.foundation.layout.BoxScopeInstance;
import androidx.compose.foundation.layout.BoxWithConstraintsKt;
import androidx.compose.foundation.layout.BoxWithConstraintsScope;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.OffsetKt;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.PaddingValues;
import androidx.compose.foundation.layout.RowKt;
import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.layout.RowScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.SpacerKt;
import androidx.compose.foundation.layout.WindowInsetsPadding_androidKt;
import androidx.compose.foundation.lazy.LazyDslKt;
import androidx.compose.foundation.lazy.LazyItemScope;
import androidx.compose.foundation.lazy.LazyListScope;
import androidx.compose.foundation.lazy.LazyListState;
import androidx.compose.foundation.lazy.LazyListStateKt;
import androidx.compose.foundation.text.BasicTextFieldKt;
import androidx.compose.material3.CardColors;
import androidx.compose.material3.CardDefaults;
import androidx.compose.material3.CardElevation;
import androidx.compose.material3.CardKt;
import androidx.compose.material3.IconKt;
import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableInferredTarget;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.DisposableEffectResult;
import androidx.compose.runtime.DisposableEffectScope;
import androidx.compose.runtime.EffectsKt;
import androidx.compose.runtime.IntState;
import androidx.compose.runtime.MutableIntState;
import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.SnapshotIntStateKt;
import androidx.compose.runtime.SnapshotStateKt;
import androidx.compose.runtime.State;
import androidx.compose.runtime.Updater;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.runtime.internal.StabilityInferred;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.ZIndexModifierKt;
import androidx.compose.ui.draw.AlphaKt;
import androidx.compose.ui.draw.CacheDrawScope;
import androidx.compose.ui.draw.ClipKt;
import androidx.compose.ui.draw.DrawModifierKt;
import androidx.compose.ui.draw.DrawResult;
import androidx.compose.ui.draw.ShadowKt;
import androidx.compose.ui.geometry.Offset;
import androidx.compose.ui.graphics.Brush;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.graphics.ColorKt;
import androidx.compose.ui.graphics.GraphicsLayerModifierKt;
import androidx.compose.ui.graphics.GraphicsLayerScope;
import androidx.compose.ui.graphics.Shadow;
import androidx.compose.ui.graphics.Shape;
import androidx.compose.ui.graphics.drawscope.ContentDrawScope;
import androidx.compose.ui.graphics.drawscope.DrawScope;
import androidx.compose.ui.graphics.painter.Painter;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.compose.ui.platform.CompositionLocalsKt;
import androidx.compose.ui.res.PainterResources_androidKt;
import androidx.compose.ui.res.StringResources_androidKt;
import androidx.compose.ui.semantics.Role;
import androidx.compose.ui.text.TextRangeKt;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.text.input.TextFieldValue;
import androidx.compose.ui.text.style.TextOverflow;
import androidx.compose.ui.unit.Density;
import androidx.compose.ui.unit.Dp;
import androidx.compose.ui.unit.DpKt;
import androidx.compose.ui.unit.IntRect;
import androidx.compose.ui.unit.TextUnitKt;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleOwnerKt;
import com.han.nomemo.AddMemorySheetKt;
import com.han.nomemo.ArchivedMemoryActivity;
import com.han.nomemo.BaseComposeActivity;
import com.han.nomemo.ComposeUiKt;
import com.han.nomemo.GroupActivity;
import com.han.nomemo.GroupActivity$GroupAddExistingMemorySheet$lambda$331$lambda$330$lambda$329$lambda$328$lambda$327$lambda$326$;
import com.han.nomemo.GroupActivity$GroupContent$lambda$259$lambda$258$lambda$257$lambda$188$lambda$162$lambda$161$;
import com.han.nomemo.GroupActivity$GroupContent$lambda$259$lambda$258$lambda$257$lambda$188$lambda$182$lambda$181$;
import com.han.nomemo.GroupActivityKt;
import com.han.nomemo.GroupAiOrganizeWorkScheduler;
import com.han.nomemo.GroupAlbumStore;
import com.han.nomemo.GroupAutoClassifySupportKt;
import com.han.nomemo.LiquidGlassDockKt;
import com.han.nomemo.MainActivity;
import com.han.nomemo.MemoryDetailActivity;
import com.han.nomemo.MemoryRecord;
import com.han.nomemo.MemoryStore;
import com.han.nomemo.NoMemoAdaptiveSpec;
import com.han.nomemo.NoMemoDockTab;
import com.han.nomemo.NoMemoMenuActionItem;
import com.han.nomemo.NoMemoPalette;
import com.han.nomemo.NoMemoSheetDragController;
import com.han.nomemo.NoMemoWidthClass;
import com.han.nomemo.R;
import com.han.nomemo.ReminderActivity;
import com.han.nomemo.SearchActivity;
import com.han.nomemo.SelectionUiKt;
import com.han.nomemo.SettingsActivity;
import com.han.nomemo.SettingsStore;
import com.kyant.backdrop.Backdrop;
import com.kyant.backdrop.backdrops.LayerBackdrop;
import com.kyant.backdrop.backdrops.LayerBackdropKt;
import com.kyant.backdrop.backdrops.LayerBackdropModifierKt;
import com.kyant.capsule.ContinuousRoundedRectangle;
import java.lang.invoke.LambdaMetafactory;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.collections.SetsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.functions.Function4;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.DelayKt;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.Job;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
@Metadata(mv={2, 2, 0}, k=1, xi=48, d1={"\u0000\u00d5\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u000b\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0002\b\n\n\u0002\u0010\b\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0012\n\u0002\u0010\"\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u000e\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u000e\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u000f\n\u0002\u0018\u0002\n\u0002\b\u0013\n\u0002\u0018\u0002\n\u0002\b\u0003*\u00018\b\u0007\u0018\u00002\u00020\u0001B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\b\u0010:\u001a\u00020\u001bH\u0014J\u0012\u0010;\u001a\u00020<2\b\u0010=\u001a\u0004\u0018\u00010>H\u0014J\b\u0010?\u001a\u00020<H\u0014J\b\u0010@\u001a\u00020<H\u0014J\b\u0010A\u001a\u00020<H\u0014J\b\u0010B\u001a\u00020<H\u0002J\b\u0010C\u001a\u00020<H\u0002J\b\u0010D\u001a\u00020<H\u0002J\b\u0010E\u001a\u00020<H\u0002J\b\u0010F\u001a\u00020<H\u0002J\u0010\u0010G\u001a\u00020<2\u0006\u0010H\u001a\u00020\u0007H\u0002J\u0010\u0010I\u001a\u00020<2\u0006\u0010J\u001a\u00020\u0007H\u0002J\b\u0010K\u001a\u00020<H\u0002J\b\u0010L\u001a\u00020<H\u0002J\u0010\u0010M\u001a\u00020<2\u0006\u0010N\u001a\u00020\u0014H\u0002J\u0016\u0010O\u001a\u00020<2\f\u0010P\u001a\b\u0012\u0004\u0012\u00020\u00070QH\u0002J\u00a7\u0002\u0010R\u001a\u00020<2\f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00140\u00132\u0006\u0010\u001c\u001a\u00020\u001b2\b\u0010\r\u001a\u0004\u0018\u00010\u00072\u0014\u0010S\u001a\u0010\u0012\u0006\u0012\u0004\u0018\u00010\u0007\u0012\u0004\u0012\u00020<0T2\u0012\u0010U\u001a\u000e\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020<0T2\u0018\u0010V\u001a\u0014\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00070Q\u0012\u0004\u0012\u00020<0T2\u0012\u0010W\u001a\u000e\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020<0T2\f\u0010X\u001a\b\u0012\u0004\u0012\u00020<0Y2\f\u0010Z\u001a\b\u0012\u0004\u0012\u00020<0Y2\f\u0010[\u001a\b\u0012\u0004\u0012\u00020<0Y2\f\u0010\\\u001a\b\u0012\u0004\u0012\u00020<0Y2\u0006\u0010\"\u001a\u00020\u001b2\f\u0010]\u001a\b\u0012\u0004\u0012\u00020<0Y2\f\u0010^\u001a\b\u0012\u0004\u0012\u00020<0Y2\b\u00102\u001a\u0004\u0018\u0001032\u0006\u0010'\u001a\u00020&2\b\u0010\u0006\u001a\u0004\u0018\u00010\u00072\u0006\u0010_\u001a\u00020\u001b2\u0012\u0010`\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020<0T2\f\u0010a\u001a\b\u0012\u0004\u0012\u00020<0YH\u0003\u00a2\u0006\u0002\u0010bJK\u0010c\u001a\u00020<2\u0006\u0010d\u001a\u00020e2\u0006\u0010f\u001a\u00020\u001b2\u0006\u0010g\u001a\u00020&2\f\u0010h\u001a\b\u0012\u0004\u0012\u00020\u00140\u00132\b\b\u0002\u0010i\u001a\u00020j2\f\u0010k\u001a\b\u0012\u0004\u0012\u00020<0YH\u0003\u00a2\u0006\u0002\u0010lJM\u0010m\u001a\u00020<2\u0006\u0010J\u001a\u00020\u00072\u0006\u0010n\u001a\u00020\u00072\f\u0010h\u001a\b\u0012\u0004\u0012\u00020\u00140\u00132\u0006\u0010g\u001a\u00020&2\u0006\u0010o\u001a\u00020\u00072\u0006\u0010f\u001a\u00020\u001b2\b\b\u0002\u0010i\u001a\u00020jH\u0003\u00a2\u0006\u0002\u0010pJ'\u0010q\u001a\u00020<2\u0006\u0010r\u001a\u00020\u00072\u0006\u0010f\u001a\u00020\u001b2\b\b\u0002\u0010i\u001a\u00020jH\u0003\u00a2\u0006\u0002\u0010sJ\u001f\u0010t\u001a\u00020<2\u0006\u0010u\u001a\u00020\u001b2\b\b\u0002\u0010i\u001a\u00020jH\u0003\u00a2\u0006\u0002\u0010vJ3\u0010w\u001a\u00020<2\b\u0010N\u001a\u0004\u0018\u00010\u00142\u0006\u0010x\u001a\u00020y2\u0006\u0010z\u001a\u00020{2\b\b\u0002\u0010i\u001a\u00020jH\u0003\u00a2\u0006\u0004\b|\u0010}J\u0081\u0001\u0010~\u001a\u00020<*\u00020\u007f2\r\u0010\u0080\u0001\u001a\b\u0012\u0004\u0012\u00020\u00140\u00132\r\u0010\u0081\u0001\u001a\b\u0012\u0004\u0012\u00020\u00070Q2\u0007\u0010\u0082\u0001\u001a\u00020\u00072\u0013\u0010\u0083\u0001\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020<0T2\u0013\u0010\u0084\u0001\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020<0T2\r\u0010\u0085\u0001\u001a\b\u0012\u0004\u0012\u00020<0Y2\r\u0010\u0086\u0001\u001a\b\u0012\u0004\u0012\u00020\u001b0YH\u0003\u00a2\u0006\u0003\u0010\u0087\u0001J\u009c\u0001\u0010\u0088\u0001\u001a\u00020<*\u00020\u007f2\u0007\u0010\u0089\u0001\u001a\u00020\u00072\u0006\u0010n\u001a\u00020\u00072\u0007\u0010\u008a\u0001\u001a\u00020\u00072\u0007\u0010\u008b\u0001\u001a\u00020\u001b2\u0007\u0010\u008c\u0001\u001a\u00020\u001b2\u0013\u0010\u008d\u0001\u001a\u000e\u0012\u0004\u0012\u00020\u001b\u0012\u0004\u0012\u00020<0T2\u0013\u0010\u008e\u0001\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020<0T2\u0013\u0010\u008f\u0001\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020<0T2\r\u0010\u0085\u0001\u001a\b\u0012\u0004\u0012\u00020<0Y2\r\u0010\u0086\u0001\u001a\b\u0012\u0004\u0012\u00020\u001b0YH\u0003\u00a2\u0006\u0003\u0010\u0090\u0001J\u0012\u0010\u0091\u0001\u001a\u0005\u0018\u00010\u0092\u0001*\u00030\u0093\u0001H\u0082\u0010JN\u0010\u0094\u0001\u001a\u00020<2\u0007\u0010\u0095\u0001\u001a\u00020\u00072\u0013\u0010\u0096\u0001\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020<0T2\u0007\u0010\u0097\u0001\u001a\u00020\u00072\t\b\u0002\u0010\u0098\u0001\u001a\u00020y2\b\b\u0002\u0010i\u001a\u00020jH\u0003\u00a2\u0006\u0006\b\u0099\u0001\u0010\u009a\u0001J6\u0010\u009b\u0001\u001a\u00020<2\u0007\u0010\u009c\u0001\u001a\u00020\u001b2\r\u0010\u009d\u0001\u001a\b\u0012\u0004\u0012\u00020<0Y2\r\u0010\u009e\u0001\u001a\b\u0012\u0004\u0012\u00020<0YH\u0003\u00a2\u0006\u0003\u0010\u009f\u0001JG\u0010\u00a0\u0001\u001a\u00020<2\u0006\u0010r\u001a\u00020\u00072\b\u0010\u00a1\u0001\u001a\u00030\u00a2\u00012\b\u0010\u00a3\u0001\u001a\u00030\u00a2\u00012\t\b\u0002\u0010\u00a4\u0001\u001a\u00020\u001b2\f\u0010k\u001a\b\u0012\u0004\u0012\u00020<0YH\u0003\u00a2\u0006\u0006\b\u00a5\u0001\u0010\u00a6\u0001J\u001a\u0010\u00a7\u0001\u001a\u00030\u00a8\u00012\u0006\u0010J\u001a\u00020\u00072\u0006\u0010u\u001a\u00020\u001bH\u0002J\u001d\u0010\u00a9\u0001\u001a\u00030\u00a8\u00012\t\u0010\u00aa\u0001\u001a\u0004\u0018\u00010\u00072\u0006\u0010u\u001a\u00020\u001bH\u0002J\u0013\u0010\u00ab\u0001\u001a\u00020\u00072\b\u0010N\u001a\u0004\u0018\u00010\u0014H\u0002J;\u0010\u00ac\u0001\u001a\u00020<2\u0006\u0010r\u001a\u00020\u00072\u0007\u0010\u00ad\u0001\u001a\u00020\u001b2\b\u0010\u00ae\u0001\u001a\u00030\u00af\u00012\f\u0010k\u001a\b\u0012\u0004\u0012\u00020<0YH\u0003\u00a2\u0006\u0006\b\u00b0\u0001\u0010\u00b1\u0001J\u001b\u0010\u00b2\u0001\u001a\u00020\u00072\u0007\u0010\u00b3\u0001\u001a\u00020\u00072\u0007\u0010\u00b4\u0001\u001a\u00020&H\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082.\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u0006\u001a\u0004\u0018\u00010\u00078BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\n\u0010\u000b\u001a\u0004\b\b\u0010\tR/\u0010\r\u001a\u0004\u0018\u00010\u00072\b\u0010\f\u001a\u0004\u0018\u00010\u00078B@BX\u0082\u008e\u0002\u00a2\u0006\u0012\n\u0004\b\u0011\u0010\u0012\u001a\u0004\b\u000e\u0010\t\"\u0004\b\u000f\u0010\u0010R7\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00140\u00132\f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00140\u00138B@BX\u0082\u008e\u0002\u00a2\u0006\u0012\n\u0004\b\u001a\u0010\u0012\u001a\u0004\b\u0016\u0010\u0017\"\u0004\b\u0018\u0010\u0019R+\u0010\u001c\u001a\u00020\u001b2\u0006\u0010\f\u001a\u00020\u001b8B@BX\u0082\u008e\u0002\u00a2\u0006\u0012\n\u0004\b!\u0010\u0012\u001a\u0004\b\u001d\u0010\u001e\"\u0004\b\u001f\u0010 R+\u0010\"\u001a\u00020\u001b2\u0006\u0010\f\u001a\u00020\u001b8B@BX\u0082\u008e\u0002\u00a2\u0006\u0012\n\u0004\b%\u0010\u0012\u001a\u0004\b#\u0010\u001e\"\u0004\b$\u0010 R+\u0010'\u001a\u00020&2\u0006\u0010\f\u001a\u00020&8B@BX\u0082\u008e\u0002\u00a2\u0006\u0012\n\u0004\b,\u0010-\u001a\u0004\b(\u0010)\"\u0004\b*\u0010+R\u000e\u0010.\u001a\u00020\u001bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010/\u001a\u0004\u0018\u000100X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u00101\u001a\u00020\u001bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u00102\u001a\u0004\u0018\u000103X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u00104\u001a\b\u0012\u0004\u0012\u00020605X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u00107\u001a\u000208X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u00109\u00a8\u0006\u00b5\u0001\u00b2\u0006\u0011\u0010\u00b6\u0001\u001a\b\u0012\u0004\u0012\u00020e0\u0013X\u008a\u008e\u0002\u00b2\u0006\r\u0010\u00b7\u0001\u001a\u0004\u0018\u00010\u0007X\u008a\u008e\u0002\u00b2\u0006\u000b\u0010\u00b8\u0001\u001a\u00020\u001bX\u008a\u008e\u0002\u00b2\u0006\u000b\u0010\u00b9\u0001\u001a\u00020\u001bX\u008a\u008e\u0002\u00b2\u0006\u0011\u0010\u00ba\u0001\u001a\b\u0012\u0004\u0012\u00020\u00070QX\u008a\u008e\u0002\u00b2\u0006\u000b\u0010\u00bb\u0001\u001a\u00020\u0007X\u008a\u008e\u0002\u00b2\u0006\u000b\u0010\u00bc\u0001\u001a\u00020\u001bX\u008a\u008e\u0002\u00b2\u0006\u000b\u0010\u00bd\u0001\u001a\u00020\u001bX\u008a\u008e\u0002\u00b2\u0006\u000e\u0010\u00be\u0001\u001a\u0005\u0018\u00010\u00bf\u0001X\u008a\u008e\u0002\u00b2\u0006\u000e\u0010\u00c0\u0001\u001a\u0005\u0018\u00010\u00bf\u0001X\u008a\u008e\u0002\u00b2\u0006\u0011\u0010\u00c1\u0001\u001a\b\u0012\u0004\u0012\u00020\u00070QX\u008a\u008e\u0002\u00b2\u0006\u000b\u0010\u00c2\u0001\u001a\u00020\u001bX\u008a\u008e\u0002\u00b2\u0006\u000b\u0010\u00c3\u0001\u001a\u00020\u001bX\u008a\u008e\u0002\u00b2\u0006\u000b\u0010\u00c4\u0001\u001a\u00020\u001bX\u008a\u008e\u0002\u00b2\u0006\u000b\u0010\u00c5\u0001\u001a\u00020\u001bX\u008a\u008e\u0002\u00b2\u0006\u000b\u0010\u00c6\u0001\u001a\u00020\u001bX\u008a\u008e\u0002\u00b2\u0006\u000b\u0010\u00c7\u0001\u001a\u00020\u001bX\u008a\u008e\u0002\u00b2\u0006\r\u0010\u00c8\u0001\u001a\u0004\u0018\u00010\u0007X\u008a\u008e\u0002\u00b2\u0006\u000b\u0010\u00c9\u0001\u001a\u00020\u0007X\u008a\u008e\u0002\u00b2\u0006\u000b\u0010\u00ca\u0001\u001a\u00020\u0007X\u008a\u008e\u0002\u00b2\u0006\u000b\u0010\u00cb\u0001\u001a\u00020\u001bX\u008a\u008e\u0002\u00b2\u0006\u000b\u0010\u00cc\u0001\u001a\u00020{X\u008a\u0084\u0002\u00b2\u0006\u000b\u0010\u00cd\u0001\u001a\u00020{X\u008a\u0084\u0002\u00b2\u0006\u000b\u0010\u00ce\u0001\u001a\u00020yX\u008a\u0084\u0002\u00b2\u0006\u000b\u0010\u00cf\u0001\u001a\u00020yX\u008a\u0084\u0002\u00b2\u0006\u000b\u0010\u00d0\u0001\u001a\u00020\u001bX\u008a\u008e\u0002\u00b2\u0006\u000b\u0010\u00d1\u0001\u001a\u00020\u001bX\u008a\u008e\u0002\u00b2\u0006\u000b\u0010\u00d0\u0001\u001a\u00020\u001bX\u008a\u008e\u0002\u00b2\u0006\u000b\u0010\u00d1\u0001\u001a\u00020\u001bX\u008a\u008e\u0002\u00b2\u0006\f\u0010\u00d2\u0001\u001a\u00030\u00d3\u0001X\u008a\u008e\u0002\u00b2\u0006\f\u0010\u00d4\u0001\u001a\u00030\u00d3\u0001X\u008a\u008e\u0002\u00b2\u0006\u000b\u0010\u00d5\u0001\u001a\u00020{X\u008a\u0084\u0002\u00b2\u0006\u000b\u0010\u00d6\u0001\u001a\u00020\u001bX\u008a\u0084\u0002"}, d2={"Lcom/han/nomemo/GroupActivity;", "Lcom/han/nomemo/BaseComposeActivity;", "<init>", "()V", "memoryStore", "Lcom/han/nomemo/MemoryStore;", "initialOpenedAlbumId", "", "getInitialOpenedAlbumId", "()Ljava/lang/String;", "initialOpenedAlbumId$delegate", "Lkotlin/Lazy;", "<set-?>", "selectedCategoryCode", "getSelectedCategoryCode", "setSelectedCategoryCode", "(Ljava/lang/String;)V", "selectedCategoryCode$delegate", "Landroidx/compose/runtime/MutableState;", "", "Lcom/han/nomemo/MemoryRecord;", "allRecords", "getAllRecords", "()Ljava/util/List;", "setAllRecords", "(Ljava/util/List;)V", "allRecords$delegate", "", "hasLoadedRecords", "getHasLoadedRecords", "()Z", "setHasLoadedRecords", "(Z)V", "hasLoadedRecords$delegate", "showAddSheet", "getShowAddSheet", "setShowAddSheet", "showAddSheet$delegate", "", "albumRefreshTick", "getAlbumRefreshTick", "()I", "setAlbumRefreshTick", "(I)V", "albumRefreshTick$delegate", "Landroidx/compose/runtime/MutableIntState;", "memoryChangeRegistered", "refreshJob", "Lkotlinx/coroutines/Job;", "hasHandledInitialResume", "startupDockPulseTab", "Lcom/han/nomemo/NoMemoDockTab;", "settingsLauncher", "Landroidx/activity/result/ActivityResultLauncher;", "Landroid/content/Intent;", "memoryChangeReceiver", "com/han/nomemo/GroupActivity$memoryChangeReceiver$1", "Lcom/han/nomemo/GroupActivity$memoryChangeReceiver$1;", "enableDoubleBackToDesktop", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "onResume", "onStart", "onStop", "refreshContent", "registerMemoryChangeReceiver", "unregisterMemoryChangeReceiver", "openMemoryPage", "openReminderPage", "openDetailPage", "recordId", "openAlbumDetailPage", "albumId", "openSettingsPage", "openSearchPage", "deleteRecord", "record", "deleteRecords", "recordIds", "", "GroupContent", "onSelectCategory", "Lkotlin/Function1;", "onDeleteRecord", "onDeleteRecords", "onOpenDetail", "onOpenMemory", "Lkotlin/Function0;", "onOpenReminder", "onOpenSearch", "onOpenSettings", "onAddClick", "onDismissAddSheet", "openedAsStandaloneDetail", "onOpenAlbumDetail", "onCloseAlbumDetail", "(Ljava/util/List;ZLjava/lang/String;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;ZLkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Lcom/han/nomemo/NoMemoDockTab;ILjava/lang/String;ZLkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function0;Landroidx/compose/runtime/Composer;III)V", "GroupAlbumGridCard", "album", "Lcom/han/nomemo/GroupAlbumStore$GroupAlbum;", "compact", "memoryCount", "previewRecords", "modifier", "Landroidx/compose/ui/Modifier;", "onClick", "(Lcom/han/nomemo/GroupAlbumStore$GroupAlbum;ZILjava/util/List;Landroidx/compose/ui/Modifier;Lkotlin/jvm/functions/Function0;Landroidx/compose/runtime/Composer;II)V", "GroupAlbumCoverCollage", "albumName", "dayText", "(Ljava/lang/String;Ljava/lang/String;Ljava/util/List;ILjava/lang/String;ZLandroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;II)V", "GroupAlbumFoilTitle", "text", "(Ljava/lang/String;ZLandroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;II)V", "GroupAlbumPaperTexture", "isDark", "(ZLandroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;II)V", "GroupAlbumCoverTile", "cornerRadius", "Landroidx/compose/ui/unit/Dp;", "rotationZ", "", "GroupAlbumCoverTile-DzVHIIc", "(Lcom/han/nomemo/MemoryRecord;FFLandroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;II)V", "GroupAddExistingMemorySheet", "Landroidx/compose/foundation/layout/BoxScope;", "records", "selectedRecordIds", "searchQuery", "onSearchQueryChange", "onToggleRecord", "onDismiss", "onConfirm", "(Landroidx/compose/foundation/layout/BoxScope;Ljava/util/List;Ljava/util/Set;Ljava/lang/String;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Landroidx/compose/runtime/Composer;I)V", "GroupEditAlbumSheet", "title", "albumDescription", "showOrganizeToggle", "autoClassifyEnabled", "onAutoClassifyEnabledChange", "onNameChange", "onDescriptionChange", "(Landroidx/compose/foundation/layout/BoxScope;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZZLkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Landroidx/compose/runtime/Composer;II)V", "findActivity", "Landroid/app/Activity;", "Landroid/content/Context;", "GroupAlbumInputField", "value", "onValueChange", "placeholder", "minHeight", "GroupAlbumInputField-hGBTI10", "(Ljava/lang/String;Lkotlin/jvm/functions/Function1;Ljava/lang/String;FLandroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;II)V", "GroupAlbumDetailEmptyState", "organizeProcessing", "onAddMemoryClick", "onOrganizeClick", "(ZLkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Landroidx/compose/runtime/Composer;I)V", "GroupAlbumEmptyActionButton", "containerColor", "Landroidx/compose/ui/graphics/Color;", "contentColor", "enabled", "GroupAlbumEmptyActionButton-DTcfvLk", "(Ljava/lang/String;JJZLkotlin/jvm/functions/Function0;Landroidx/compose/runtime/Composer;II)V", "groupAlbumCoverBrush", "Landroidx/compose/ui/graphics/Brush;", "groupAlbumFallbackTileBrush", "categoryCode", "groupAlbumFallbackTileLabel", "GroupChip", "selected", "chipTextSize", "Landroidx/compose/ui/unit/TextUnit;", "GroupChip-a5Y-_hM", "(Ljava/lang/String;ZJLkotlin/jvm/functions/Function0;Landroidx/compose/runtime/Composer;I)V", "buildChipText", "label", "count", "app", "albumList", "openedAlbumId", "showCreateAlbumDialog", "showAddExistingSheet", "selectedExistingRecordIds", "addExistingSearchQuery", "groupListMoreExpanded", "detailMoreExpanded", "groupListMoreAnchorBounds", "Landroidx/compose/ui/unit/IntRect;", "detailMoreAnchorBounds", "selectedAlbumRecordIds", "albumSelectionModeActive", "showRemoveFromAlbumConfirm", "showDeleteSelectedConfirm", "showEditAlbumDialog", "showDeleteAlbumConfirm", "closingStandaloneDetail", "editingAlbumId", "albumNameInput", "albumDescriptionInput", "albumAutoClassifyEnabledInput", "groupHeaderCollapseTarget", "groupHeaderCollapseProgress", "groupExpandedTitleHeight", "groupListTopPadding", "visible", "dismissCommitted", "albumNameField", "Landroidx/compose/ui/text/input/TextFieldValue;", "albumDescriptionField", "alpha", "pressed"})
@StabilityInferred(parameters=0)
@SourceDebugExtension(value={"SMAP\nGroupActivity.kt\nKotlin\n*S Kotlin\n*F\n+ 1 GroupActivity.kt\ncom/han/nomemo/GroupActivity\n+ 2 SnapshotState.kt\nandroidx/compose/runtime/SnapshotStateKt__SnapshotStateKt\n+ 3 SnapshotIntState.kt\nandroidx/compose/runtime/SnapshotIntStateKt__SnapshotIntStateKt\n+ 4 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 5 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 6 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 7 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 8 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 9 Box.kt\nandroidx/compose/foundation/layout/BoxKt\n+ 10 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 11 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 12 Composer.kt\nandroidx/compose/runtime/Updater\n+ 13 Dp.kt\nandroidx/compose/ui/unit/Dp\n+ 14 Row.kt\nandroidx/compose/foundation/layout/RowKt\n+ 15 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 16 Offset.kt\nandroidx/compose/ui/geometry/OffsetKt\n+ 17 InlineClassHelper.kt\nandroidx/compose/ui/util/InlineClassHelperKt\n+ 18 LazyDsl.kt\nandroidx/compose/foundation/lazy/LazyDslKt\n+ 19 Size.kt\nandroidx/compose/ui/geometry/Size\n+ 20 InlineClassHelper.jvm.kt\nandroidx/compose/ui/util/InlineClassHelper_jvmKt\n+ 21 Effects.kt\nandroidx/compose/runtime/DisposableEffectScope\n*L\n1#1,2485:1\n85#2:2486\n117#2,2:2487\n85#2:2489\n117#2,2:2490\n85#2:2492\n117#2,2:2493\n85#2:2495\n117#2,2:2496\n85#2:3673\n117#2,2:3674\n85#2:3676\n117#2,2:3677\n85#2:3679\n117#2,2:3680\n85#2:3682\n117#2,2:3683\n85#2:3685\n117#2,2:3686\n85#2:3688\n117#2,2:3689\n85#2:3691\n117#2,2:3692\n85#2:3694\n117#2,2:3695\n85#2:3697\n117#2,2:3698\n85#2:3700\n117#2,2:3701\n85#2:3703\n117#2,2:3704\n85#2:3706\n117#2,2:3707\n85#2:3709\n117#2,2:3710\n85#2:3712\n117#2,2:3713\n85#2:3715\n117#2,2:3716\n85#2:3718\n117#2,2:3719\n85#2:3721\n117#2,2:3722\n85#2:3724\n117#2,2:3725\n85#2:3727\n117#2,2:3728\n85#2:3730\n117#2,2:3731\n85#2:3733\n117#2,2:3734\n85#2:3736\n85#2:3737\n85#2:3738\n85#2:3739\n85#2:4534\n117#2,2:4535\n85#2:4537\n117#2,2:4538\n85#2:4844\n117#2,2:4845\n85#2:4847\n117#2,2:4848\n85#2:4850\n117#2,2:4851\n85#2:4853\n117#2,2:4854\n85#2:5194\n85#2:5195\n78#3:2498\n111#3,2:2499\n1915#4,2:2501\n1586#4:2645\n1661#4,3:2646\n1220#4,2:2667\n1249#4,4:2669\n1205#4,2:2679\n1282#4,2:2681\n1642#4,10:2683\n1915#4:2693\n1916#4:2695\n1652#4:2696\n1285#4:2697\n296#4,2:2704\n296#4,2:2712\n1220#4,2:2720\n1249#4,4:2722\n1642#4,10:2726\n1915#4:2736\n1916#4:2738\n1652#4:2739\n777#4:2746\n873#4,2:2747\n1786#4,3:2752\n832#4:2758\n862#4,2:2759\n777#4:2767\n873#4,2:2768\n1586#4:3753\n1661#4,3:3754\n1586#4:3770\n1661#4,3:3771\n1807#4,3:3774\n1807#4,3:3777\n75#5:2503\n75#5:2773\n75#5:3329\n1282#6,6:2504\n1282#6,6:2510\n1282#6,6:2516\n1282#6,6:2522\n1282#6,6:2528\n1282#6,6:2534\n1282#6,6:2540\n1282#6,6:2546\n1282#6,6:2552\n1282#6,6:2558\n1282#6,6:2564\n1282#6,6:2570\n1282#6,6:2576\n1282#6,6:2582\n1282#6,6:2588\n1282#6,6:2594\n1282#6,6:2600\n1282#6,6:2606\n1282#6,6:2612\n1282#6,6:2618\n1282#6,6:2624\n1282#6,6:2630\n1282#6,6:2636\n1282#6,3:2642\n1285#6,3:2649\n1282#6,6:2652\n1282#6,6:2658\n1282#6,3:2664\n1285#6,3:2673\n1282#6,3:2676\n1285#6,3:2698\n1282#6,3:2701\n1285#6,3:2706\n1282#6,3:2709\n1285#6,3:2714\n1282#6,3:2717\n1285#6,3:2740\n1282#6,3:2743\n1285#6,3:2749\n1282#6,3:2755\n1285#6,3:2761\n1282#6,3:2764\n1285#6,3:2770\n1282#6,6:2776\n1282#6,6:2787\n1282#6,6:2793\n1282#6,6:2799\n1282#6,6:2805\n1282#6,6:2811\n1282#6,6:2817\n1282#6,6:2823\n1282#6,6:2829\n1282#6,6:2835\n1282#6,6:2841\n1282#6,6:2893\n1282#6,6:2902\n1282#6,6:3214\n1282#6,6:3222\n1282#6,6:3234\n1282#6,6:3240\n1282#6,6:3246\n1282#6,6:3252\n1282#6,6:3258\n1282#6,6:3264\n1282#6,6:3270\n1282#6,6:3313\n1282#6,6:3319\n1282#6,6:3330\n1282#6,6:3339\n1282#6,6:3345\n1282#6,6:3351\n1282#6,6:3357\n1282#6,6:3363\n1282#6,6:3369\n1282#6,6:3375\n1282#6,6:3381\n1282#6,6:3387\n1282#6,6:3393\n1282#6,6:3399\n1282#6,6:3405\n1282#6,6:3448\n1282#6,6:3454\n1282#6,6:3511\n1282#6,6:3601\n1282#6,6:3607\n1282#6,6:3613\n1282#6,6:3619\n1282#6,6:3625\n1282#6,6:3631\n1282#6,6:3637\n1282#6,6:3643\n1282#6,6:3649\n1282#6,6:3655\n1282#6,6:3661\n1282#6,6:3667\n1282#6,6:3817\n1282#6,6:3900\n1282#6,6:3906\n1282#6,6:3912\n1282#6,6:3918\n1282#6,6:3929\n1282#6,6:4022\n1282#6,6:4065\n1282#6,6:4071\n1282#6,6:4157\n1282#6,6:4163\n1282#6,6:4169\n1282#6,6:4189\n1282#6,6:4231\n1282#6,6:4237\n1282#6,6:4252\n1282#6,6:4259\n1282#6,6:4265\n1282#6,6:4271\n1282#6,6:4277\n1282#6,6:4283\n1282#6,6:4289\n1282#6,6:4295\n1282#6,6:4301\n1282#6,6:4307\n1282#6,6:4313\n1282#6,6:4319\n1282#6,6:4325\n1282#6,6:4331\n1282#6,6:4337\n1282#6,6:4343\n1282#6,6:4349\n1282#6,6:4355\n1282#6,6:4361\n1282#6,6:4367\n1282#6,6:4373\n1282#6,6:4379\n1282#6,6:4385\n1282#6,6:4391\n1282#6,6:4397\n1282#6,6:4403\n1282#6,6:4409\n1282#6,6:4415\n1282#6,6:4421\n1282#6,6:4427\n1282#6,6:4433\n1282#6,6:4439\n1282#6,6:4540\n1282#6,6:4546\n1282#6,6:4634\n1282#6,6:4733\n1282#6,6:4831\n1282#6,6:4861\n1282#6,6:4867\n1282#6,6:4915\n1282#6,6:4963\n1282#6,6:5045\n1#7:2694\n1#7:2737\n1#7:2774\n113#8:2775\n113#8:2782\n113#8:2783\n113#8:2784\n113#8:2785\n113#8:2786\n113#8:2847\n113#8:2848\n113#8:2849\n113#8:2850\n113#8:2851\n113#8:2852\n113#8:2890\n113#8:2891\n113#8:2892\n113#8:2899\n113#8:2901\n113#8:2908\n113#8:2947\n113#8:3032\n113#8:3033\n113#8:3034\n113#8:3076\n113#8:3114\n113#8:3152\n113#8:3153\n113#8:3154\n118#8:3204\n118#8:3205\n113#8:3220\n113#8:3228\n113#8:3229\n113#8:3230\n113#8:3231\n113#8:3232\n113#8:3233\n113#8:3336\n113#8:3337\n113#8:3338\n113#8:3464\n113#8:3465\n113#8:3466\n113#8:3467\n113#8:3468\n113#8:3502\n113#8:3503\n113#8:3504\n113#8:3505\n113#8:3506\n113#8:3517\n113#8:3600\n113#8:3823\n113#8:3825\n113#8:3928\n113#8:3976\n113#8:3977\n113#8:3979\n113#8:4020\n113#8:4081\n113#8:4082\n113#8:4120\n113#8:4122\n113#8:4183\n113#8:4184\n113#8:4186\n113#8:4188\n113#8:4251\n113#8:4258\n113#8:4449\n113#8:4450\n113#8:4485\n113#8:4486\n113#8:4487\n113#8:4488\n113#8:4490\n113#8:4529\n113#8:4592\n113#8:4593\n113#8:4594\n113#8:4632\n113#8:4633\n113#8:4657\n113#8:4695\n113#8:4739\n113#8:4785\n113#8:4786\n113#8:4827\n113#8:4828\n113#8:4830\n113#8:4841\n113#8:4842\n113#8:4843\n113#8:4913\n113#8:4914\n113#8:4961\n113#8:4962\n113#8:4969\n113#8:5007\n113#8:5051\n113#8:5134\n113#8:5135\n113#8:5136\n113#8:5137\n113#8:5138\n113#8:5139\n113#8:5140\n113#8:5150\n113#8:5151\n113#8:5152\n113#8:5193\n113#8:5196\n70#9:2853\n67#9,9:2854\n70#9:2910\n67#9,9:2911\n77#9:2951\n70#9:3035\n67#9,9:3036\n77#9:3075\n77#9:3166\n70#9:3167\n67#9,9:3168\n77#9:3213\n70#9:3276\n67#9,9:3277\n77#9:3328\n70#9:3411\n67#9,9:3412\n77#9:3463\n70#9:3518\n67#9,9:3519\n77#9:3558\n70#9:3559\n67#9,9:3560\n77#9:3599\n70#9:3780\n67#9,9:3781\n70#9:3863\n67#9,9:3864\n77#9:3927\n70#9:3980\n68#9,8:3981\n77#9:4019\n70#9:4028\n67#9,9:4029\n77#9:4080\n70#9:4083\n67#9,9:4084\n77#9:4182\n70#9:4195\n68#9,8:4196\n77#9:4246\n77#9:4448\n70#9:4492\n67#9,9:4493\n77#9:4533\n70#9:4552\n68#9,8:4553\n77#9:4591\n70#9:4787\n68#9,8:4788\n77#9:4826\n70#9:4873\n68#9,8:4874\n77#9:4912\n70#9:4921\n68#9,8:4922\n77#9:4960\n70#9:5153\n68#9,8:5154\n77#9:5192\n70#9:5197\n68#9,8:5198\n77#9:5236\n80#10,6:2863\n87#10,3:2878\n90#10,2:2887\n80#10,6:2920\n87#10,3:2935\n90#10,2:2944\n94#10:2950\n80#10,6:2961\n87#10,3:2976\n90#10,2:2985\n80#10,6:2997\n87#10,3:3012\n90#10,2:3021\n94#10:3026\n94#10:3030\n80#10,6:3045\n87#10,3:3060\n90#10,2:3069\n94#10:3074\n80#10,6:3087\n87#10,3:3102\n90#10,2:3111\n80#10,6:3125\n87#10,3:3140\n90#10,2:3149\n94#10:3157\n94#10:3161\n94#10:3165\n80#10,6:3177\n87#10,3:3192\n90#10,2:3201\n94#10:3212\n80#10,6:3286\n87#10,3:3301\n90#10,2:3310\n94#10:3327\n80#10,6:3421\n87#10,3:3436\n90#10,2:3445\n94#10:3462\n80#10,6:3475\n87#10,3:3490\n90#10,2:3499\n94#10:3509\n80#10,6:3528\n87#10,3:3543\n90#10,2:3552\n94#10:3557\n80#10,6:3569\n87#10,3:3584\n90#10,2:3593\n94#10:3598\n80#10,6:3790\n87#10,3:3805\n90#10,2:3814\n80#10,6:3836\n87#10,3:3851\n90#10,2:3860\n80#10,6:3873\n87#10,3:3888\n90#10,2:3897\n94#10:3926\n80#10,6:3945\n87#10,3:3960\n90#10,2:3969\n94#10:3974\n80#10,6:3989\n87#10,3:4004\n90#10,2:4013\n94#10:4018\n80#10,6:4038\n87#10,3:4053\n90#10,2:4062\n94#10:4079\n80#10,6:4093\n87#10,3:4108\n90#10,2:4117\n80#10,6:4130\n87#10,3:4145\n90#10,2:4154\n94#10:4177\n94#10:4181\n80#10,6:4204\n87#10,3:4219\n90#10,2:4228\n94#10:4245\n94#10:4249\n94#10:4447\n80#10,6:4502\n87#10,3:4517\n90#10,2:4526\n94#10:4532\n80#10,6:4561\n87#10,3:4576\n90#10,2:4585\n94#10:4590\n80#10,6:4605\n87#10,3:4620\n90#10,2:4629\n94#10:4642\n80#10,6:4668\n87#10,3:4683\n90#10,2:4692\n80#10,6:4706\n87#10,3:4721\n90#10,2:4730\n80#10,6:4750\n87#10,3:4765\n90#10,2:4774\n94#10:4779\n94#10:4783\n80#10,6:4796\n87#10,3:4811\n90#10,2:4820\n94#10:4825\n94#10:4839\n80#10,6:4882\n87#10,3:4897\n90#10,2:4906\n94#10:4911\n80#10,6:4930\n87#10,3:4945\n90#10,2:4954\n94#10:4959\n80#10,6:4980\n87#10,3:4995\n90#10,2:5004\n80#10,6:5018\n87#10,3:5033\n90#10,2:5042\n80#10,6:5062\n87#10,3:5077\n90#10,2:5086\n94#10:5091\n94#10:5095\n80#10,6:5107\n87#10,3:5122\n90#10,2:5131\n94#10:5144\n94#10:5148\n80#10,6:5162\n87#10,3:5177\n90#10,2:5186\n94#10:5191\n80#10,6:5206\n87#10,3:5221\n90#10,2:5230\n94#10:5235\n391#11,9:2869\n400#11:2889\n391#11,9:2926\n400#11:2946\n401#11,2:2948\n391#11,9:2967\n400#11:2987\n391#11,9:3003\n400#11,3:3023\n401#11,2:3028\n391#11,9:3051\n400#11,3:3071\n391#11,9:3093\n400#11:3113\n391#11,9:3131\n400#11:3151\n401#11,2:3155\n401#11,2:3159\n401#11,2:3163\n391#11,9:3183\n400#11:3203\n401#11,2:3210\n391#11,9:3292\n400#11:3312\n401#11,2:3325\n391#11,9:3427\n400#11:3447\n401#11,2:3460\n391#11,9:3481\n400#11:3501\n401#11,2:3507\n391#11,9:3534\n400#11,3:3554\n391#11,9:3575\n400#11,3:3595\n391#11,9:3796\n400#11:3816\n391#11,9:3842\n400#11:3862\n391#11,9:3879\n400#11:3899\n401#11,2:3924\n391#11,9:3951\n400#11,3:3971\n391#11,9:3995\n400#11,3:4015\n391#11,9:4044\n400#11:4064\n401#11,2:4077\n391#11,9:4099\n400#11:4119\n391#11,9:4136\n400#11:4156\n401#11,2:4175\n401#11,2:4179\n391#11,9:4210\n400#11:4230\n401#11,2:4243\n401#11,2:4247\n401#11,2:4445\n391#11,9:4508\n400#11:4528\n401#11,2:4530\n391#11,9:4567\n400#11,3:4587\n391#11,9:4611\n400#11:4631\n401#11,2:4640\n391#11,9:4674\n400#11:4694\n391#11,9:4712\n400#11:4732\n391#11,9:4756\n400#11,3:4776\n401#11,2:4781\n391#11,9:4802\n400#11,3:4822\n401#11,2:4837\n391#11,9:4888\n400#11,3:4908\n391#11,9:4936\n400#11,3:4956\n391#11,9:4986\n400#11:5006\n391#11,9:5024\n400#11:5044\n391#11,9:5068\n400#11,3:5088\n401#11,2:5093\n391#11,9:5113\n400#11:5133\n401#11,2:5142\n401#11,2:5146\n391#11,9:5168\n400#11,3:5188\n391#11,9:5212\n400#11,3:5232\n4360#12,6:2881\n4360#12,6:2938\n4360#12,6:2979\n4360#12,6:3015\n4360#12,6:3063\n4360#12,6:3105\n4360#12,6:3143\n4360#12,6:3195\n4360#12,6:3304\n4360#12,6:3439\n4360#12,6:3493\n4360#12,6:3546\n4360#12,6:3587\n4360#12,6:3808\n4360#12,6:3854\n4360#12,6:3891\n4360#12,6:3963\n4360#12,6:4007\n4360#12,6:4056\n4360#12,6:4111\n4360#12,6:4148\n4360#12,6:4222\n4360#12,6:4520\n4360#12,6:4579\n4360#12,6:4623\n4360#12,6:4686\n4360#12,6:4724\n4360#12,6:4768\n4360#12,6:4814\n4360#12,6:4900\n4360#12,6:4948\n4360#12,6:4998\n4360#12,6:5036\n4360#12,6:5080\n4360#12,6:5125\n4360#12,6:5180\n4360#12,6:5224\n49#13:2900\n49#13:2909\n52#13:3221\n52#13:3824\n49#13:3978\n49#13:4021\n49#13:4121\n49#13:4123\n49#13:4185\n49#13:4187\n52#13:4489\n52#13:4491\n49#13:4829\n49#13:5141\n99#14:2952\n97#14,8:2953\n106#14:3031\n99#14:3115\n96#14,9:3116\n106#14:3158\n99#14,6:4124\n106#14:4178\n99#14:4595\n96#14,9:4596\n106#14:4643\n99#14:4696\n96#14,9:4697\n106#14:4784\n99#14:5008\n96#14,9:5009\n106#14:5096\n87#15:2988\n85#15,8:2989\n94#15:3027\n87#15:3077\n84#15,9:3078\n94#15:3162\n87#15,6:3469\n94#15:3510\n87#15:3826\n84#15,9:3827\n87#15:3935\n84#15,9:3936\n94#15:3975\n94#15:4250\n87#15:4658\n84#15,9:4659\n87#15:4740\n84#15,9:4741\n94#15:4780\n94#15:4840\n87#15:4970\n84#15,9:4971\n87#15:5052\n84#15,9:5053\n94#15:5092\n87#15:5097\n84#15,9:5098\n94#15:5145\n94#15:5149\n30#16:3206\n30#16:4454\n30#16:4461\n30#16:4474\n30#16:4481\n53#17,3:3207\n60#17:4452\n53#17,3:4455\n70#17:4459\n53#17,3:4462\n70#17:4466\n60#17:4469\n70#17:4472\n53#17,3:4475\n70#17:4479\n53#17,3:4482\n168#18,13:3740\n168#18,13:3757\n168#18,13:4644\n57#19:4451\n61#19:4458\n61#19:4465\n57#19:4468\n61#19:4471\n61#19:4478\n22#20:4453\n22#20:4460\n22#20:4467\n22#20:4470\n22#20:4473\n22#20:4480\n66#21,5:4856\n*S KotlinDebug\n*F\n+ 1 GroupActivity.kt\ncom/han/nomemo/GroupActivity\n*L\n114#1:2486\n114#1:2487,2\n115#1:2489\n115#1:2490,2\n116#1:2492\n116#1:2493,2\n117#1:2495\n117#1:2496,2\n317#1:3673\n317#1:3674,2\n318#1:3676\n318#1:3677,2\n319#1:3679\n319#1:3680,2\n320#1:3682\n320#1:3683,2\n321#1:3685\n321#1:3686,2\n322#1:3688\n322#1:3689,2\n323#1:3691\n323#1:3692,2\n324#1:3694\n324#1:3695,2\n325#1:3697\n325#1:3698,2\n326#1:3700\n326#1:3701,2\n327#1:3703\n327#1:3704,2\n328#1:3706\n328#1:3707,2\n329#1:3709\n329#1:3710,2\n330#1:3712\n330#1:3713,2\n331#1:3715\n331#1:3716,2\n332#1:3718\n332#1:3719,2\n333#1:3721\n333#1:3722,2\n334#1:3724\n334#1:3725,2\n335#1:3727\n335#1:3728,2\n336#1:3730\n336#1:3731,2\n337#1:3733\n337#1:3734,2\n401#1:3736\n420#1:3737\n430#1:3738\n436#1:3739\n1668#1:4534\n1668#1:4535,2\n1669#1:4537\n1669#1:4538,2\n1939#1:4844\n1939#1:4845,2\n1940#1:4847\n1940#1:4848,2\n1941#1:4850\n1941#1:4851,2\n1944#1:4853\n1944#1:4854,2\n2345#1:5194\n2351#1:5195\n118#1:2498\n118#1:2499,2\n276#1:2501,2\n338#1:2645\n338#1:2646,3\n346#1:2667,2\n346#1:2669,4\n348#1:2679,2\n348#1:2681,2\n350#1:2683,10\n350#1:2693\n350#1:2695\n350#1:2696\n348#1:2697\n355#1:2704,2\n359#1:2712,2\n366#1:2720,2\n366#1:2722,4\n367#1:2726,10\n367#1:2736\n367#1:2738\n367#1:2739\n370#1:2746\n370#1:2747,2\n373#1:2752,3\n377#1:2758\n377#1:2759,2\n384#1:2767\n384#1:2768,2\n671#1:3753\n671#1:3754,3\n908#1:3770\n908#1:3771,3\n979#1:3774,3\n1061#1:3777,3\n310#1:2503\n399#1:2773\n1917#1:3329\n311#1:2504,6\n312#1:2510,6\n317#1:2516,6\n318#1:2522,6\n319#1:2528,6\n320#1:2534,6\n321#1:2540,6\n322#1:2546,6\n323#1:2552,6\n324#1:2558,6\n325#1:2564,6\n326#1:2570,6\n327#1:2576,6\n328#1:2582,6\n329#1:2588,6\n330#1:2594,6\n331#1:2600,6\n332#1:2606,6\n333#1:2612,6\n334#1:2618,6\n335#1:2624,6\n336#1:2630,6\n337#1:2636,6\n338#1:2642,3\n338#1:2649,3\n339#1:2652,6\n345#1:2658,6\n346#1:2664,3\n346#1:2673,3\n347#1:2676,3\n347#1:2698,3\n354#1:2701,3\n354#1:2706,3\n357#1:2709,3\n357#1:2714,3\n364#1:2717,3\n364#1:2740,3\n369#1:2743,3\n369#1:2749,3\n374#1:2755,3\n374#1:2761,3\n379#1:2764,3\n379#1:2770,3\n401#1:2776,6\n441#1:2787,6\n449#1:2793,6\n455#1:2799,6\n463#1:2805,6\n483#1:2811,6\n491#1:2817,6\n497#1:2823,6\n502#1:2829,6\n512#1:2835,6\n1169#1:2841,6\n1264#1:2893,6\n1278#1:2902,6\n1495#1:3214,6\n1567#1:3222,6\n1668#1:3234,6\n1669#1:3240,6\n1671#1:3246,6\n1675#1:3252,6\n1683#1:3258,6\n1689#1:3264,6\n1698#1:3270,6\n1731#1:3313,6\n1735#1:3319,6\n1918#1:3330,6\n1939#1:3339,6\n1940#1:3345,6\n1941#1:3351,6\n1944#1:3357,6\n1948#1:3363,6\n1953#1:3369,6\n1962#1:3375,6\n1973#1:3381,6\n1974#1:3387,6\n1982#1:3393,6\n1988#1:3399,6\n1997#1:3405,6\n2028#1:3448,6\n2032#1:3454,6\n2350#1:3511,6\n159#1:3601,6\n160#1:3607,6\n161#1:3613,6\n162#1:3619,6\n163#1:3625,6\n164#1:3631,6\n165#1:3637,6\n166#1:3643,6\n168#1:3649,6\n169#1:3655,6\n174#1:3661,6\n175#1:3667,6\n520#1:3817,6\n544#1:3900,6\n548#1:3906,6\n549#1:3912,6\n559#1:3918,6\n569#1:3929,6\n608#1:4022,6\n644#1:4065,6\n667#1:4071,6\n709#1:4157,6\n721#1:4163,6\n725#1:4169,6\n744#1:4189,6\n795#1:4231,6\n800#1:4237,6\n830#1:4252,6\n851#1:4259,6\n852#1:4265,6\n867#1:4271,6\n873#1:4277,6\n881#1:4283,6\n889#1:4289,6\n899#1:4295,6\n905#1:4301,6\n916#1:4307,6\n926#1:4313,6\n940#1:4319,6\n955#1:4325,6\n956#1:4331,6\n957#1:4337,6\n958#1:4343,6\n964#1:4349,6\n1003#1:4355,6\n1004#1:4361,6\n1011#1:4367,6\n1016#1:4373,6\n1046#1:4379,6\n1047#1:4385,6\n1048#1:4391,6\n1049#1:4397,6\n1053#1:4403,6\n1078#1:4409,6\n1097#1:4415,6\n1113#1:4421,6\n1129#1:4427,6\n1137#1:4433,6\n1143#1:4439,6\n1721#1:4540,6\n1723#1:4546,6\n1846#1:4634,6\n1773#1:4733,6\n1879#1:4831,6\n2018#1:4861,6\n2020#1:4867,6\n2116#1:4915,6\n2161#1:4963,6\n2070#1:5045,6\n350#1:2694\n367#1:2737\n400#1:2775\n428#1:2782\n429#1:2783\n431#1:2784\n435#1:2785\n437#1:2786\n1172#1:2847\n1213#1:2848\n1214#1:2849\n1215#1:2850\n1216#1:2851\n1217#1:2852\n1229#1:2890\n1230#1:2891\n1261#1:2892\n1267#1:2899\n1275#1:2901\n1281#1:2908\n1300#1:2947\n1362#1:3032\n1372#1:3033\n1376#1:3034\n1389#1:3076\n1396#1:3114\n1405#1:3152\n1408#1:3153\n1412#1:3154\n1439#1:3204\n1448#1:3205\n1563#1:3220\n1571#1:3228\n1578#1:3229\n1582#1:3230\n1662#1:3231\n1663#1:3232\n1666#1:3233\n1932#1:3336\n1933#1:3337\n1936#1:3338\n2223#1:3464\n2230#1:3465\n2238#1:3466\n2284#1:3467\n2285#1:3468\n2293#1:3502\n2295#1:3503\n2302#1:3504\n2309#1:3505\n2316#1:3506\n2352#1:3517\n2465#1:3600\n531#1:3823\n533#1:3825\n568#1:3928\n583#1:3976\n584#1:3977\n590#1:3979\n605#1:4020\n680#1:4081\n685#1:4082\n697#1:4120\n698#1:4122\n736#1:4183\n738#1:4184\n740#1:4186\n743#1:4188\n842#1:4251\n860#1:4258\n1194#1:4449\n1184#1:4450\n1496#1:4485\n1497#1:4486\n1498#1:4487\n1585#1:4488\n1586#1:4490\n1616#1:4529\n1852#1:4592\n1808#1:4593\n1809#1:4594\n1816#1:4632\n1828#1:4633\n1756#1:4657\n1767#1:4695\n1779#1:4739\n1801#1:4785\n1802#1:4786\n1875#1:4827\n1876#1:4828\n1878#1:4830\n1746#1:4841\n1747#1:4842\n1749#1:4843\n2128#1:4913\n2129#1:4914\n2172#1:4961\n2173#1:4962\n2053#1:4969\n2064#1:5007\n2076#1:5051\n2105#1:5134\n2110#1:5135\n2111#1:5136\n2152#1:5137\n2156#1:5138\n2198#1:5139\n2202#1:5140\n2043#1:5150\n2044#1:5151\n2046#1:5152\n2253#1:5193\n2367#1:5196\n1219#1:2853\n1219#1:2854,9\n1289#1:2910\n1289#1:2911,9\n1289#1:2951\n1359#1:3035\n1359#1:3036,9\n1359#1:3075\n1219#1:3166\n1431#1:3167\n1431#1:3168,9\n1431#1:3213\n1702#1:3276\n1702#1:3277,9\n1702#1:3328\n1999#1:3411\n1999#1:3412,9\n1999#1:3463\n2380#1:3518\n2380#1:3519,9\n2380#1:3558\n2392#1:3559\n2392#1:3560,9\n2392#1:3599\n518#1:3780\n518#1:3781,9\n537#1:3863\n537#1:3864,9\n537#1:3927\n585#1:3980\n585#1:3981,8\n585#1:4019\n636#1:4028\n636#1:4029,9\n636#1:4080\n682#1:4083\n682#1:4084,9\n682#1:4182\n787#1:4195\n787#1:4196,8\n787#1:4246\n518#1:4448\n1587#1:4492\n1587#1:4493,9\n1587#1:4533\n1830#1:4552\n1830#1:4553,8\n1830#1:4591\n1860#1:4787\n1860#1:4788,8\n1860#1:4826\n2131#1:4873\n2131#1:4874,8\n2131#1:4912\n2175#1:4921\n2175#1:4922,8\n2175#1:4960\n2255#1:5153\n2255#1:5154,8\n2255#1:5192\n2364#1:5197\n2364#1:5198,8\n2364#1:5236\n1219#1:2863,6\n1219#1:2878,3\n1219#1:2887,2\n1289#1:2920,6\n1289#1:2935,3\n1289#1:2944,2\n1289#1:2950\n1304#1:2961,6\n1304#1:2976,3\n1304#1:2985,2\n1318#1:2997,6\n1318#1:3012,3\n1318#1:3021,2\n1318#1:3026\n1304#1:3030\n1359#1:3045,6\n1359#1:3060,3\n1359#1:3069,2\n1359#1:3074\n1386#1:3087,6\n1386#1:3102,3\n1386#1:3111,2\n1395#1:3125,6\n1395#1:3140,3\n1395#1:3149,2\n1395#1:3157\n1386#1:3161\n1219#1:3165\n1431#1:3177,6\n1431#1:3192,3\n1431#1:3201,2\n1431#1:3212\n1702#1:3286,6\n1702#1:3301,3\n1702#1:3310,2\n1702#1:3327\n1999#1:3421,6\n1999#1:3436,3\n1999#1:3445,2\n1999#1:3462\n2281#1:3475,6\n2281#1:3490,3\n2281#1:3499,2\n2281#1:3509\n2380#1:3528,6\n2380#1:3543,3\n2380#1:3552,2\n2380#1:3557\n2392#1:3569,6\n2392#1:3584,3\n2392#1:3593,2\n2392#1:3598\n518#1:3790,6\n518#1:3805,3\n518#1:3814,2\n524#1:3836,6\n524#1:3851,3\n524#1:3860,2\n537#1:3873,6\n537#1:3888,3\n537#1:3897,2\n537#1:3926\n564#1:3945,6\n564#1:3960,3\n564#1:3969,2\n564#1:3974\n585#1:3989,6\n585#1:4004,3\n585#1:4013,2\n585#1:4018\n636#1:4038,6\n636#1:4053,3\n636#1:4062,2\n636#1:4079\n682#1:4093,6\n682#1:4108,3\n682#1:4117,2\n701#1:4130,6\n701#1:4145,3\n701#1:4154,2\n701#1:4177\n682#1:4181\n787#1:4204,6\n787#1:4219,3\n787#1:4228,2\n787#1:4245\n524#1:4249\n518#1:4447\n1587#1:4502,6\n1587#1:4517,3\n1587#1:4526,2\n1587#1:4532\n1830#1:4561,6\n1830#1:4576,3\n1830#1:4585,2\n1830#1:4590\n1805#1:4605,6\n1805#1:4620,3\n1805#1:4629,2\n1805#1:4642\n1752#1:4668,6\n1752#1:4683,3\n1752#1:4692,2\n1764#1:4706,6\n1764#1:4721,3\n1764#1:4730,2\n1776#1:4750,6\n1776#1:4765,3\n1776#1:4774,2\n1776#1:4779\n1764#1:4783\n1860#1:4796,6\n1860#1:4811,3\n1860#1:4820,2\n1860#1:4825\n1752#1:4839\n2131#1:4882,6\n2131#1:4897,3\n2131#1:4906,2\n2131#1:4911\n2175#1:4930,6\n2175#1:4945,3\n2175#1:4954,2\n2175#1:4959\n2049#1:4980,6\n2049#1:4995,3\n2049#1:5004,2\n2061#1:5018,6\n2061#1:5033,3\n2061#1:5042,2\n2073#1:5062,6\n2073#1:5077,3\n2073#1:5086,2\n2073#1:5091\n2061#1:5095\n2095#1:5107,6\n2095#1:5122,3\n2095#1:5131,2\n2095#1:5144\n2049#1:5148\n2255#1:5162,6\n2255#1:5177,3\n2255#1:5186,2\n2255#1:5191\n2364#1:5206,6\n2364#1:5221,3\n2364#1:5230,2\n2364#1:5235\n1219#1:2869,9\n1219#1:2889\n1289#1:2926,9\n1289#1:2946\n1289#1:2948,2\n1304#1:2967,9\n1304#1:2987\n1318#1:3003,9\n1318#1:3023,3\n1304#1:3028,2\n1359#1:3051,9\n1359#1:3071,3\n1386#1:3093,9\n1386#1:3113\n1395#1:3131,9\n1395#1:3151\n1395#1:3155,2\n1386#1:3159,2\n1219#1:3163,2\n1431#1:3183,9\n1431#1:3203\n1431#1:3210,2\n1702#1:3292,9\n1702#1:3312\n1702#1:3325,2\n1999#1:3427,9\n1999#1:3447\n1999#1:3460,2\n2281#1:3481,9\n2281#1:3501\n2281#1:3507,2\n2380#1:3534,9\n2380#1:3554,3\n2392#1:3575,9\n2392#1:3595,3\n518#1:3796,9\n518#1:3816\n524#1:3842,9\n524#1:3862\n537#1:3879,9\n537#1:3899\n537#1:3924,2\n564#1:3951,9\n564#1:3971,3\n585#1:3995,9\n585#1:4015,3\n636#1:4044,9\n636#1:4064\n636#1:4077,2\n682#1:4099,9\n682#1:4119\n701#1:4136,9\n701#1:4156\n701#1:4175,2\n682#1:4179,2\n787#1:4210,9\n787#1:4230\n787#1:4243,2\n524#1:4247,2\n518#1:4445,2\n1587#1:4508,9\n1587#1:4528\n1587#1:4530,2\n1830#1:4567,9\n1830#1:4587,3\n1805#1:4611,9\n1805#1:4631\n1805#1:4640,2\n1752#1:4674,9\n1752#1:4694\n1764#1:4712,9\n1764#1:4732\n1776#1:4756,9\n1776#1:4776,3\n1764#1:4781,2\n1860#1:4802,9\n1860#1:4822,3\n1752#1:4837,2\n2131#1:4888,9\n2131#1:4908,3\n2175#1:4936,9\n2175#1:4956,3\n2049#1:4986,9\n2049#1:5006\n2061#1:5024,9\n2061#1:5044\n2073#1:5068,9\n2073#1:5088,3\n2061#1:5093,2\n2095#1:5113,9\n2095#1:5133\n2095#1:5142,2\n2049#1:5146,2\n2255#1:5168,9\n2255#1:5188,3\n2364#1:5212,9\n2364#1:5232,3\n1219#1:2881,6\n1289#1:2938,6\n1304#1:2979,6\n1318#1:3015,6\n1359#1:3063,6\n1386#1:3105,6\n1395#1:3143,6\n1431#1:3195,6\n1702#1:3304,6\n1999#1:3439,6\n2281#1:3493,6\n2380#1:3546,6\n2392#1:3587,6\n518#1:3808,6\n524#1:3854,6\n537#1:3891,6\n564#1:3963,6\n585#1:4007,6\n636#1:4056,6\n682#1:4111,6\n701#1:4148,6\n787#1:4222,6\n1587#1:4520,6\n1830#1:4579,6\n1805#1:4623,6\n1752#1:4686,6\n1764#1:4724,6\n1776#1:4768,6\n1860#1:4814,6\n2131#1:4900,6\n2175#1:4948,6\n2049#1:4998,6\n2061#1:5036,6\n2073#1:5080,6\n2095#1:5125,6\n2255#1:5180,6\n2364#1:5224,6\n1267#1:2900\n1281#1:2909\n1563#1:3221\n531#1:3824\n590#1:3978\n605#1:4021\n697#1:4121\n698#1:4123\n738#1:4185\n740#1:4187\n1585#1:4489\n1586#1:4491\n1876#1:4829\n2202#1:5141\n1304#1:2952\n1304#1:2953,8\n1304#1:3031\n1395#1:3115\n1395#1:3116,9\n1395#1:3158\n701#1:4124,6\n701#1:4178\n1805#1:4595\n1805#1:4596,9\n1805#1:4643\n1764#1:4696\n1764#1:4697,9\n1764#1:4784\n2061#1:5008\n2061#1:5009,9\n2061#1:5096\n1318#1:2988\n1318#1:2989,8\n1318#1:3027\n1386#1:3077\n1386#1:3078,9\n1386#1:3162\n2281#1:3469,6\n2281#1:3510\n524#1:3826\n524#1:3827,9\n564#1:3935\n564#1:3936,9\n564#1:3975\n524#1:4250\n1752#1:4658\n1752#1:4659,9\n1776#1:4740\n1776#1:4741,9\n1776#1:4780\n1752#1:4840\n2049#1:4970\n2049#1:4971,9\n2073#1:5052\n2073#1:5053,9\n2073#1:5092\n2095#1:5097\n2095#1:5098,9\n2095#1:5145\n2049#1:5149\n1460#1:3206\n1517#1:4454\n1518#1:4461\n1528#1:4474\n1529#1:4481\n1460#1:3207,3\n1514#1:4452\n1517#1:4455,3\n1518#1:4459\n1518#1:4462,3\n1524#1:4466\n1525#1:4469\n1528#1:4472\n1528#1:4475,3\n1529#1:4479\n1529#1:4482,3\n609#1:3740,13\n745#1:3757,13\n1880#1:4644,13\n1514#1:4451\n1518#1:4458\n1524#1:4465\n1525#1:4468\n1528#1:4471\n1529#1:4478\n1514#1:4453\n1518#1:4460\n1524#1:4467\n1525#1:4470\n1528#1:4473\n1529#1:4480\n1966#1:4856,5\n*E\n"})
public final class GroupActivity
extends BaseComposeActivity {
    private MemoryStore memoryStore;
    @NotNull
    private final Lazy initialOpenedAlbumId$delegate = LazyKt.lazy(() -> GroupActivity.initialOpenedAlbumId_delegate$lambda$1(this));
    @NotNull
    private final MutableState selectedCategoryCode$delegate = SnapshotStateKt.mutableStateOf$default(null, null, (int)2, null);
    @NotNull
    private final MutableState allRecords$delegate = SnapshotStateKt.mutableStateOf$default((Object)CollectionsKt.emptyList(), null, (int)2, null);
    @NotNull
    private final MutableState hasLoadedRecords$delegate = SnapshotStateKt.mutableStateOf$default((Object)false, null, (int)2, null);
    @NotNull
    private final MutableState showAddSheet$delegate = SnapshotStateKt.mutableStateOf$default((Object)false, null, (int)2, null);
    @NotNull
    private final MutableIntState albumRefreshTick$delegate = SnapshotIntStateKt.mutableIntStateOf((int)0);
    private boolean memoryChangeRegistered;
    @Nullable
    private Job refreshJob;
    private boolean hasHandledInitialResume;
    @Nullable
    private NoMemoDockTab startupDockPulseTab;
    @NotNull
    private final ActivityResultLauncher<Intent> settingsLauncher = this.registerForActivityResult((ActivityResultContract)new ActivityResultContracts.StartActivityForResult(), arg_0 -> GroupActivity.settingsLauncher$lambda$2(this, arg_0));
    @NotNull
    private final memoryChangeReceiver.1 memoryChangeReceiver = new BroadcastReceiver(this){
        final /* synthetic */ GroupActivity this$0;
        {
            this.this$0 = $receiver;
        }

        public void onReceive(Context context, Intent intent) {
            GroupActivity.access$refreshContent(this.this$0);
            Intent intent2 = intent;
            if (Intrinsics.areEqual((Object)(intent2 != null ? intent2.getAction() : null), (Object)"com.han.nomemo.ACTION_GROUP_ALBUMS_CHANGED")) {
                GroupActivity.access$setAlbumRefreshTick(this.this$0, GroupActivity.access$getAlbumRefreshTick(this.this$0) + 1);
            }
        }
    };
    public static final int $stable = 8;

    private final String getInitialOpenedAlbumId() {
        Lazy lazy = this.initialOpenedAlbumId$delegate;
        return (String)lazy.getValue();
    }

    /*
     * WARNING - void declaration
     */
    private final String getSelectedCategoryCode() {
        void $this$getValue\1;
        State state = (State)this.selectedCategoryCode$delegate;
        GroupActivity groupActivity = this;
        Object property\1 = null;
        boolean bl = false;
        return (String)$this$getValue\1.getValue();
    }

    /*
     * WARNING - void declaration
     */
    private final void setSelectedCategoryCode(String string2) {
        void $this$setValue\1;
        MutableState mutableState = this.selectedCategoryCode$delegate;
        GroupActivity groupActivity = this;
        Object var4_4 = null;
        String string3 = string2;
        boolean bl = false;
        $this$setValue\1.setValue((Object)string3);
    }

    /*
     * WARNING - void declaration
     */
    private final List<MemoryRecord> getAllRecords() {
        void $this$getValue\1;
        State state = (State)this.allRecords$delegate;
        GroupActivity groupActivity = this;
        Object property\1 = null;
        boolean bl = false;
        return (List)$this$getValue\1.getValue();
    }

    /*
     * WARNING - void declaration
     */
    private final void setAllRecords(List<? extends MemoryRecord> list) {
        void $this$setValue\1;
        MutableState mutableState = this.allRecords$delegate;
        GroupActivity groupActivity = this;
        Object var4_4 = null;
        List<? extends MemoryRecord> list2 = list;
        boolean bl = false;
        $this$setValue\1.setValue(list2);
    }

    /*
     * WARNING - void declaration
     */
    private final boolean getHasLoadedRecords() {
        void $this$getValue\1;
        State state = (State)this.hasLoadedRecords$delegate;
        GroupActivity groupActivity = this;
        Object property\1 = null;
        boolean bl = false;
        return (Boolean)$this$getValue\1.getValue();
    }

    /*
     * WARNING - void declaration
     */
    private final void setHasLoadedRecords(boolean bl) {
        void $this$setValue\1;
        MutableState mutableState = this.hasLoadedRecords$delegate;
        GroupActivity groupActivity = this;
        Object var4_4 = null;
        Boolean bl2 = bl;
        boolean bl3 = false;
        $this$setValue\1.setValue((Object)bl2);
    }

    /*
     * WARNING - void declaration
     */
    private final boolean getShowAddSheet() {
        void $this$getValue\1;
        State state = (State)this.showAddSheet$delegate;
        GroupActivity groupActivity = this;
        Object property\1 = null;
        boolean bl = false;
        return (Boolean)$this$getValue\1.getValue();
    }

    /*
     * WARNING - void declaration
     */
    private final void setShowAddSheet(boolean bl) {
        void $this$setValue\1;
        MutableState mutableState = this.showAddSheet$delegate;
        GroupActivity groupActivity = this;
        Object var4_4 = null;
        Boolean bl2 = bl;
        boolean bl3 = false;
        $this$setValue\1.setValue((Object)bl2);
    }

    /*
     * WARNING - void declaration
     */
    private final int getAlbumRefreshTick() {
        void $this$getValue\1;
        IntState intState = (IntState)this.albumRefreshTick$delegate;
        GroupActivity groupActivity = this;
        Object property\1 = null;
        boolean bl = false;
        return $this$getValue\1.getIntValue();
    }

    /*
     * WARNING - void declaration
     */
    private final void setAlbumRefreshTick(int n) {
        void $this$setValue\1;
        MutableIntState mutableIntState = this.albumRefreshTick$delegate;
        GroupActivity groupActivity = this;
        Object var4_4 = null;
        int n2 = n;
        boolean bl = false;
        $this$setValue\1.setIntValue(n2);
    }

    @Override
    protected boolean enableDoubleBackToDesktop() {
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this.getInitialOpenedAlbumId() == null) {
            this.startActivity(MainActivity.Companion.createPrimaryTabIntent((Context)this, NoMemoDockTab.GROUP));
            this.overridePendingTransition(R.anim.primary_page_enter, R.anim.primary_page_exit);
            this.finish();
            return;
        }
        this.startupDockPulseTab = this.consumePrimaryDockPulse();
        this.memoryStore = new MemoryStore((Context)this);
        ComponentActivityKt.setContent$default((ComponentActivity)((ComponentActivity)this), null, (Function2)((Function2)ComposableLambdaKt.composableLambdaInstance((int)-626061397, (boolean)true, (arg_0, arg_1) -> GroupActivity.onCreate$lambda$27(this, arg_0, arg_1))), (int)1, null);
        this.refreshContent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!this.hasHandledInitialResume) {
            this.hasHandledInitialResume = true;
            return;
        }
        this.refreshContent();
        this.setAlbumRefreshTick(this.getAlbumRefreshTick() + 1);
    }

    protected void onStart() {
        super.onStart();
        this.registerMemoryChangeReceiver();
    }

    protected void onStop() {
        this.unregisterMemoryChangeReceiver();
        super.onStop();
    }

    private final void refreshContent() {
        Job job = this.refreshJob;
        if (job != null) {
            Job.DefaultImpls.cancel$default((Job)job, null, (int)1, null);
        }
        this.refreshJob = BuildersKt.launch$default((CoroutineScope)((CoroutineScope)LifecycleOwnerKt.getLifecycleScope((LifecycleOwner)((LifecycleOwner)this))), null, null, (Function2)((Function2)new Function2<CoroutineScope, Continuation<? super Unit>, Object>(this, null){
            int label;
            final /* synthetic */ GroupActivity this$0;
            {
                this.this$0 = $receiver;
                super(2, $completion);
            }

            /*
             * Unable to fully structure code
             */
            public final Object invokeSuspend(Object $result) {
                var3_2 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                switch (this.label) {
                    case 0: {
                        ResultKt.throwOnFailure((Object)$result);
                        this.label = 1;
                        v0 = BuildersKt.withContext((CoroutineContext)((CoroutineContext)Dispatchers.getIO()), (Function2)((Function2)new Function2<CoroutineScope, Continuation<? super List<MemoryRecord>>, Object>(this.this$0, null){
                            Object L$0;
                            int label;
                            final /* synthetic */ GroupActivity this$0;
                            {
                                this.this$0 = $receiver;
                                super(2, $completion);
                            }

                            /*
                             * Enabled force condition propagation
                             * Lifted jumps to return sites
                             */
                            public final Object invokeSuspend(Object $result) {
                                Object object = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                                switch (this.label) {
                                    case 0: {
                                        ResultKt.throwOnFailure((Object)$result);
                                        MemoryStore memoryStore = GroupActivity.access$getMemoryStore$p(this.this$0);
                                        if (memoryStore == null) {
                                            Intrinsics.throwUninitializedPropertyAccessException((String)"memoryStore");
                                            memoryStore = null;
                                        }
                                        List result = memoryStore.loadActiveRecords();
                                        Context context = this.this$0.getApplicationContext();
                                        Intrinsics.checkNotNullExpressionValue((Object)context, (String)"getApplicationContext(...)");
                                        Intrinsics.checkNotNull((Object)result);
                                        this.L$0 = result;
                                        this.label = 1;
                                        Object object2 = ComposeUiKt.prewarmMemoryThumbnailCache$default(context, result, 0, 0, 0, (Continuation)this, 28, null);
                                        if (object2 != object) return result;
                                        return object;
                                    }
                                    case 1: {
                                        List result = (List)this.L$0;
                                        ResultKt.throwOnFailure((Object)$result);
                                        Object object2 = $result;
                                        return result;
                                    }
                                }
                                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                            }

                            public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
                                return (Continuation)new /* invalid duplicate definition of identical inner class */;
                            }

                            public final Object invoke(CoroutineScope p1, Continuation<? super List<MemoryRecord>> p2) {
                                return (this.create(p1, p2)).invokeSuspend(Unit.INSTANCE);
                            }
                        }), (Continuation)((Continuation)this));
                        if (v0 == var3_2) {
                            return var3_2;
                        }
                        ** GOTO lbl13
                    }
                    case 1: {
                        ResultKt.throwOnFailure((Object)$result);
                        v0 = $result;
lbl13:
                        // 2 sources

                        loadedRecords = (List)v0;
                        Intrinsics.checkNotNull((Object)loadedRecords);
                        GroupActivity.access$setAllRecords(this.this$0, loadedRecords);
                        GroupActivity.access$setHasLoadedRecords(this.this$0, true);
                        return Unit.INSTANCE;
                    }
                }
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }

            public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
                return (Continuation)new /* invalid duplicate definition of identical inner class */;
            }

            public final Object invoke(CoroutineScope p1, Continuation<? super Unit> p2) {
                return (this.create(p1, p2)).invokeSuspend(Unit.INSTANCE);
            }
        }), (int)3, null);
    }

    /*
     * WARNING - void declaration
     */
    private final void registerMemoryChangeReceiver() {
        void $this$registerMemoryChangeReceiver_u24lambda_u2428\1;
        IntentFilter intentFilter;
        if (this.memoryChangeRegistered) {
            return;
        }
        IntentFilter intentFilter2 = intentFilter = new IntentFilter();
        BroadcastReceiver broadcastReceiver = this.memoryChangeReceiver;
        Context context = (Context)this;
        boolean bl = false;
        $this$registerMemoryChangeReceiver_u24lambda_u2428\1.addAction("com.han.nomemo.ACTION_RECORDS_CHANGED");
        $this$registerMemoryChangeReceiver_u24lambda_u2428\1.addAction("com.han.nomemo.ACTION_GROUP_ALBUMS_CHANGED");
        Unit unit = Unit.INSTANCE;
        ContextCompat.registerReceiver((Context)context, (BroadcastReceiver)broadcastReceiver, (IntentFilter)intentFilter, (int)4);
        this.memoryChangeRegistered = true;
    }

    private final void unregisterMemoryChangeReceiver() {
        if (!this.memoryChangeRegistered) {
            return;
        }
        this.unregisterReceiver(this.memoryChangeReceiver);
        this.memoryChangeRegistered = false;
    }

    private final void openMemoryPage() {
        Intent intent;
        Intent intent2 = intent = new Intent((Context)this, MainActivity.class);
        boolean bl = false;
        intent2.addFlags(0x24000000);
        Intent intent3 = intent;
        this.switchPrimaryPage(intent3, NoMemoDockTab.MEMORY);
    }

    private final void openReminderPage() {
        this.switchPrimaryPage(new Intent((Context)this, ReminderActivity.class), NoMemoDockTab.REMINDER);
    }

    private final void openDetailPage(String recordId) {
        this.startActivity(MemoryDetailActivity.Companion.createIntent$default(MemoryDetailActivity.Companion, (Context)this, recordId, false, 4, null));
    }

    private final void openAlbumDetailPage(String albumId) {
        this.startActivity(GroupActivityKt.createGroupActivityIntent((Context)this, albumId));
    }

    private final void openSettingsPage() {
        this.settingsLauncher.launch((Object)new Intent((Context)this, SettingsActivity.class));
    }

    private final void openSearchPage() {
        this.startActivity(SearchActivity.Companion.createIntent((Context)this));
    }

    private final void deleteRecord(MemoryRecord record) {
        boolean deleted;
        MemoryStore memoryStore = this.memoryStore;
        if (memoryStore == null) {
            Intrinsics.throwUninitializedPropertyAccessException((String)"memoryStore");
            memoryStore = null;
        }
        if (deleted = memoryStore.deleteRecord(record.getRecordId())) {
            this.refreshContent();
            Toast.makeText((Context)((Context)this), (int)R.string.delete_success, (int)0).show();
        }
    }

    private final void deleteRecords(Set<String> recordIds) {
        if (recordIds.isEmpty()) {
            return;
        }
        int deletedCount = 0;
        Iterable iterable = recordIds;
        boolean bl = false;
        for (Object t : iterable) {
            String string2 = (String)t;
            boolean bl2 = false;
            MemoryStore memoryStore = this.memoryStore;
            if (memoryStore == null) {
                Intrinsics.throwUninitializedPropertyAccessException((String)"memoryStore");
                memoryStore = null;
            }
            if (!memoryStore.deleteRecord(string2)) continue;
            ++deletedCount;
        }
        if (deletedCount > 0) {
            this.refreshContent();
            Object[] objectArray = new Object[]{deletedCount};
            Toast.makeText((Context)((Context)this), (CharSequence)this.getString(R.string.delete_selected_success, objectArray), (int)0).show();
        }
    }

    /*
     * Could not resolve type clashes
     * Unable to fully structure code
     */
    @Composable
    @ComposableTarget(applier="androidx.compose.ui.UiComposable")
    private final void GroupContent(List<? extends MemoryRecord> allRecords, boolean hasLoadedRecords, String selectedCategoryCode, Function1<? super String, Unit> onSelectCategory, Function1<? super MemoryRecord, Unit> onDeleteRecord, Function1<? super Set<String>, Unit> onDeleteRecords, Function1<? super MemoryRecord, Unit> onOpenDetail, Function0<Unit> onOpenMemory, Function0<Unit> onOpenReminder, Function0<Unit> onOpenSearch, Function0<Unit> onOpenSettings, boolean showAddSheet, Function0<Unit> onAddClick, Function0<Unit> onDismissAddSheet, NoMemoDockTab startupDockPulseTab, int albumRefreshTick, String initialOpenedAlbumId, boolean openedAsStandaloneDetail, Function1<? super String, Unit> onOpenAlbumDetail, Function0<Unit> onCloseAlbumDetail, Composer $composer, int $changed, int $changed1, int $changed2) {
        block136: {
            block135: {
                block134: {
                    block133: {
                        $composer = $composer.startRestartGroup(603914142);
                        ComposerKt.sourceInformation((Composer)$composer, (String)"C(GroupContent)N(allRecords,hasLoadedRecords,selectedCategoryCode,onSelectCategory,onDeleteRecord,onDeleteRecords,onOpenDetail,onOpenMemory,onOpenReminder,onOpenSearch,onOpenSettings,showAddSheet,onAddClick,onDismissAddSheet,startupDockPulseTab,albumRefreshTick,initialOpenedAlbumId,openedAsStandaloneDetail,onOpenAlbumDetail,onCloseAlbumDetail)309@12089L7,310@12122L56,311@12207L54,312@12290L28,313@12346L23,314@12399L23,315@12458L23,316@12507L52,317@12589L49,318@12676L34,319@12747L34,320@12823L52,321@12914L31,322@12983L34,323@13052L34,324@13128L68,325@13235L68,326@13342L52,327@13435L34,328@13512L34,329@13588L34,330@13658L34,331@13731L34,332@13805L34,333@13870L42,334@13943L31,335@14012L31,336@14089L34,337@14153L63,338@14258L59,338@14225L92,344@14480L85,345@14591L63,346@14692L245,353@14964L112,356@15113L210,363@15352L241,368@15629L141,373@15958L273,378@16270L759,398@17065L7,400@17189L721,419@17954L212,429@18617L257,435@18947L230,440@19235L234,440@19186L283,448@19508L197,448@19478L227,454@19780L374,454@19714L440,462@20217L542,462@20163L596,482@21073L203,474@20768L508,490@21333L197,490@21285L245,496@21608L154,496@21539L223,501@21813L50,501@21771L92,511@22134L53,504@21872L315,515@22214L37341,515@22197L37358:GroupActivity.kt#83vr7l");
                        $dirty = $changed;
                        $dirty1 = $changed1;
                        $dirty2 = $changed2;
                        if (($changed & 6) == 0) {
                            $dirty |= $composer.changedInstance(allRecords) != false ? 4 : 2;
                        }
                        if (($changed & 48) == 0) {
                            $dirty |= $composer.changed(hasLoadedRecords) != false ? 32 : 16;
                        }
                        if (($changed & 196608) == 0) {
                            $dirty |= $composer.changedInstance(onDeleteRecords) != false ? 131072 : 65536;
                        }
                        if (($changed & 0x180000) == 0) {
                            $dirty |= $composer.changedInstance(onOpenDetail) != false ? 0x100000 : 524288;
                        }
                        if (($changed & 0xC00000) == 0) {
                            $dirty |= $composer.changedInstance(onOpenMemory) != false ? 0x800000 : 0x400000;
                        }
                        if (($changed & 0x6000000) == 0) {
                            $dirty |= $composer.changedInstance(onOpenReminder) != false ? 0x4000000 : 0x2000000;
                        }
                        if (($changed & 0x30000000) == 0) {
                            $dirty |= $composer.changedInstance(onOpenSearch) != false ? 0x20000000 : 0x10000000;
                        }
                        if (($changed1 & 6) == 0) {
                            $dirty1 |= $composer.changedInstance(onOpenSettings) != false ? 4 : 2;
                        }
                        if (($changed1 & 48) == 0) {
                            $dirty1 |= $composer.changed(showAddSheet) != false ? 32 : 16;
                        }
                        if (($changed1 & 384) == 0) {
                            $dirty1 |= $composer.changedInstance(onAddClick) != false ? 256 : 128;
                        }
                        if (($changed1 & 3072) == 0) {
                            $dirty1 |= $composer.changedInstance(onDismissAddSheet) != false ? 2048 : 1024;
                        }
                        if (($changed1 & 24576) == 0) {
                            v0 = startupDockPulseTab;
                            $dirty1 |= $composer.changed(v0 == null ? -1 : ((Enum)v0).ordinal()) != false ? 16384 : 8192;
                        }
                        if (($changed1 & 196608) == 0) {
                            $dirty1 |= $composer.changed(albumRefreshTick) != false ? 131072 : 65536;
                        }
                        if (($changed1 & 0x180000) == 0) {
                            $dirty1 |= $composer.changed((Object)initialOpenedAlbumId) != false ? 0x100000 : 524288;
                        }
                        if (($changed1 & 0xC00000) == 0) {
                            $dirty1 |= $composer.changed(openedAsStandaloneDetail) != false ? 0x800000 : 0x400000;
                        }
                        if (($changed1 & 0x6000000) == 0) {
                            $dirty1 |= $composer.changedInstance(onOpenAlbumDetail) != false ? 0x4000000 : 0x2000000;
                        }
                        if (($changed1 & 0x30000000) == 0) {
                            $dirty1 |= $composer.changedInstance(onCloseAlbumDetail) != false ? 0x20000000 : 0x10000000;
                        }
                        if (($changed2 & 6) == 0) {
                            $dirty2 |= $composer.changedInstance((Object)this) != false ? 4 : 2;
                        }
                        if (!$composer.shouldExecute(($dirty & 306774035) != 306774034 || ($dirty1 & 306783379) != 306783378 || ($dirty2 & 3) != 2, $dirty & 1)) break block134;
                        if (ComposerKt.isTraceInProgress()) {
                            ComposerKt.traceEventStart((int)603914142, (int)$dirty, (int)$dirty1, (String)"com.han.nomemo.GroupActivity.GroupContent (GroupActivity.kt:308)");
                        }
                        var29_28 = (CompositionLocal)AndroidCompositionLocals_androidKt.getLocalContext();
                        var30_29 = $composer;
                        $changed\1 = false;
                        $i$f$getCurrent\1\310 = false;
                        ComposerKt.sourceInformationMarkerStart((Composer)$composer\1, (int)2023513938, (String)"CC(<get-current>):CompositionLocal.kt#9igjgp");
                        var33_34 = $composer\1.consume((CompositionLocal)this_\1);
                        ComposerKt.sourceInformationMarkerEnd((Composer)$composer\1);
                        albumContext = (Context)var33_34;
                        ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)540934614, (String)"CC(remember):GroupActivity.kt#9igjgp");
                        $changed\1 = $composer;
                        invalid\2 = $composer.changed((Object)albumContext);
                        $i$f$cache\2\311 = false;
                        it\2 = $this$cache\2.rememberedValue();
                        $i$a$-let-ComposerKt$cache$1\3\2504\2 = false;
                        if (invalid\2 || it\2 == Composer.Companion.getEmpty()) {
                            $i$a$-cache-GroupActivity$GroupContent$albumStore$1\4\2506\0 = false;
                            value\3 = new GroupAlbumStore(albumContext);
                            $this$cache\2.updateRememberedValue(value\3);
                            v1 = value\3;
                        } else {
                            v1 = it\2;
                        }
                        $composer\1 = (GroupAlbumStore)v1;
                        ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
                        albumStore = $composer\1;
                        ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)540937332, (String)"CC(remember):GroupActivity.kt#9igjgp");
                        invalid\2 = $composer;
                        invalid\5 = $composer.changed((Object)albumContext);
                        $i$f$cache\5\312 = false;
                        it\5 = $this$cache\5.rememberedValue();
                        $i$a$-let-ComposerKt$cache$1\6\2510\5 = false;
                        if (invalid\5 || it\5 == Composer.Companion.getEmpty()) {
                            $i$a$-cache-GroupActivity$GroupContent$settingsStore$1\7\2512\0 = false;
                            value\6 = new SettingsStore(albumContext);
                            $this$cache\5.updateRememberedValue((Object)value\6);
                            v2 = value\6;
                        } else {
                            v2 = it\5;
                        }
                        $this$cache\2 = (SettingsStore)v2;
                        ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
                        settingsStore = $this$cache\2;
                        albumAdaptive = ComposeUiKt.rememberNoMemoAdaptiveSpec($composer, 0);
                        albumPalette = ComposeUiKt.rememberNoMemoPalette($composer, 0);
                        groupListState = LazyListStateKt.rememberLazyListState((int)0, (int)0, (Composer)$composer, (int)0, (int)3);
                        albumDetailListState = LazyListStateKt.rememberLazyListState((int)0, (int)0, (Composer)$composer, (int)0, (int)3);
                        ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)540946930, (String)"CC(remember):GroupActivity.kt#9igjgp");
                        $i$a$-cache-GroupActivity$GroupContent$settingsStore$1\7\2512\0 = $composer;
                        invalid\8 = false;
                        $i$f$cache\8\317 = false;
                        it\8 = $this$cache\8.rememberedValue();
                        $i$a$-let-ComposerKt$cache$1\9\2516\8 = false;
                        if (it\8 == Composer.Companion.getEmpty()) {
                            $i$a$-cache-GroupActivity$GroupContent$albumList$2\10\2518\0 = false;
                            value\9 = SnapshotStateKt.mutableStateOf$default(albumStore.loadAlbums(), null, (int)2, null);
                            $this$cache\8.updateRememberedValue((Object)value\9);
                            v3 = value\9;
                        } else {
                            v3 = it\8;
                        }
                        $i$a$-let-ComposerKt$cache$1\6\2510\5 = (MutableState)v3;
                        ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
                        albumList$delegate = $i$a$-let-ComposerKt$cache$1\6\2510\5;
                        ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)540949551, (String)"CC(remember):GroupActivity.kt#9igjgp");
                        invalid\8 = $composer;
                        invalid\11 = false;
                        $i$f$cache\11\318 = false;
                        it\11 = $this$cache\11.rememberedValue();
                        $i$a$-let-ComposerKt$cache$1\12\2522\11 = false;
                        if (it\11 == Composer.Companion.getEmpty()) {
                            $i$a$-cache-GroupActivity$GroupContent$openedAlbumId$2\13\2524\0 = false;
                            value\12 = SnapshotStateKt.mutableStateOf$default((Object)initialOpenedAlbumId, null, (int)2, null);
                            $this$cache\11.updateRememberedValue((Object)value\12);
                            v4 = value\12;
                        } else {
                            v4 = it\11;
                        }
                        $this$cache\8 = (MutableState)v4;
                        ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
                        openedAlbumId$delegate = $this$cache\8;
                        ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)540952320, (String)"CC(remember):GroupActivity.kt#9igjgp");
                        invalid\11 = $composer;
                        invalid\14 = false;
                        $i$f$cache\14\319 = false;
                        it\14 = $this$cache\14.rememberedValue();
                        $i$a$-let-ComposerKt$cache$1\15\2528\14 = false;
                        if (it\14 == Composer.Companion.getEmpty()) {
                            $i$a$-cache-GroupActivity$GroupContent$showCreateAlbumDialog$2\16\2530\0 = false;
                            value\15 = SnapshotStateKt.mutableStateOf$default((Object)false, null, (int)2, null);
                            $this$cache\14.updateRememberedValue((Object)value\15);
                            v5 = value\15;
                        } else {
                            v5 = it\14;
                        }
                        $this$cache\11 = (MutableState)v5;
                        ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
                        showCreateAlbumDialog$delegate = $this$cache\11;
                        ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)540954592, (String)"CC(remember):GroupActivity.kt#9igjgp");
                        invalid\14 = $composer;
                        invalid\17 = false;
                        $i$f$cache\17\320 = false;
                        it\17 = $this$cache\17.rememberedValue();
                        $i$a$-let-ComposerKt$cache$1\18\2534\17 = false;
                        if (it\17 == Composer.Companion.getEmpty()) {
                            $i$a$-cache-GroupActivity$GroupContent$showAddExistingSheet$2\19\2536\0 = false;
                            value\18 = SnapshotStateKt.mutableStateOf$default((Object)false, null, (int)2, null);
                            $this$cache\17.updateRememberedValue((Object)value\18);
                            v6 = value\18;
                        } else {
                            v6 = it\17;
                        }
                        $this$cache\14 = (MutableState)v6;
                        ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
                        showAddExistingSheet$delegate = $this$cache\14;
                        ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)540957042, (String)"CC(remember):GroupActivity.kt#9igjgp");
                        invalid\17 = $composer;
                        invalid\20 = false;
                        $i$f$cache\20\321 = false;
                        it\20 = $this$cache\20.rememberedValue();
                        $i$a$-let-ComposerKt$cache$1\21\2540\20 = false;
                        if (it\20 == Composer.Companion.getEmpty()) {
                            $i$a$-cache-GroupActivity$GroupContent$selectedExistingRecordIds$2\22\2542\0 = false;
                            value\21 = SnapshotStateKt.mutableStateOf$default((Object)SetsKt.emptySet(), null, (int)2, null);
                            $this$cache\20.updateRememberedValue((Object)value\21);
                            v7 = value\21;
                        } else {
                            v7 = it\20;
                        }
                        $this$cache\17 = (MutableState)v7;
                        ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
                        selectedExistingRecordIds$delegate = $this$cache\17;
                        ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)540959933, (String)"CC(remember):GroupActivity.kt#9igjgp");
                        invalid\20 = $composer;
                        invalid\23 = false;
                        $i$f$cache\23\322 = false;
                        it\23 = $this$cache\23.rememberedValue();
                        $i$a$-let-ComposerKt$cache$1\24\2546\23 = false;
                        if (it\23 == Composer.Companion.getEmpty()) {
                            $i$a$-cache-GroupActivity$GroupContent$addExistingSearchQuery$2\25\2548\0 = false;
                            value\24 = SnapshotStateKt.mutableStateOf$default((Object)"", null, (int)2, null);
                            $this$cache\23.updateRememberedValue((Object)value\24);
                            v8 = value\24;
                        } else {
                            v8 = it\23;
                        }
                        $this$cache\20 = (MutableState)v8;
                        ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
                        addExistingSearchQuery$delegate = $this$cache\20;
                        ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)540962144, (String)"CC(remember):GroupActivity.kt#9igjgp");
                        invalid\23 = $composer;
                        invalid\26 = false;
                        $i$f$cache\26\323 = false;
                        it\26 = $this$cache\26.rememberedValue();
                        $i$a$-let-ComposerKt$cache$1\27\2552\26 = false;
                        if (it\26 == Composer.Companion.getEmpty()) {
                            $i$a$-cache-GroupActivity$GroupContent$groupListMoreExpanded$2\28\2554\0 = false;
                            value\27 = SnapshotStateKt.mutableStateOf$default((Object)false, null, (int)2, null);
                            $this$cache\26.updateRememberedValue((Object)value\27);
                            v9 = value\27;
                        } else {
                            v9 = it\26;
                        }
                        $this$cache\23 = (MutableState)v9;
                        ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
                        groupListMoreExpanded$delegate = $this$cache\23;
                        ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)540964352, (String)"CC(remember):GroupActivity.kt#9igjgp");
                        invalid\26 = $composer;
                        invalid\29 = false;
                        $i$f$cache\29\324 = false;
                        it\29 = $this$cache\29.rememberedValue();
                        $i$a$-let-ComposerKt$cache$1\30\2558\29 = false;
                        if (it\29 == Composer.Companion.getEmpty()) {
                            $i$a$-cache-GroupActivity$GroupContent$detailMoreExpanded$2\31\2560\0 = false;
                            value\30 = SnapshotStateKt.mutableStateOf$default((Object)false, null, (int)2, null);
                            $this$cache\29.updateRememberedValue((Object)value\30);
                            v10 = value\30;
                        } else {
                            v10 = it\29;
                        }
                        $this$cache\26 = (MutableState)v10;
                        ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
                        detailMoreExpanded$delegate = $this$cache\26;
                        ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)540966818, (String)"CC(remember):GroupActivity.kt#9igjgp");
                        invalid\29 = $composer;
                        invalid\32 = false;
                        $i$f$cache\32\325 = false;
                        it\32 = $this$cache\32.rememberedValue();
                        $i$a$-let-ComposerKt$cache$1\33\2564\32 = false;
                        if (it\32 == Composer.Companion.getEmpty()) {
                            $i$a$-cache-GroupActivity$GroupContent$groupListMoreAnchorBounds$2\34\2566\0 = false;
                            value\33 = SnapshotStateKt.mutableStateOf$default(null, null, (int)2, null);
                            $this$cache\32.updateRememberedValue((Object)value\33);
                            v11 = value\33;
                        } else {
                            v11 = it\32;
                        }
                        $this$cache\29 = (MutableState)v11;
                        ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
                        groupListMoreAnchorBounds$delegate = $this$cache\29;
                        ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)540970242, (String)"CC(remember):GroupActivity.kt#9igjgp");
                        invalid\32 = $composer;
                        invalid\35 = false;
                        $i$f$cache\35\326 = false;
                        it\35 = $this$cache\35.rememberedValue();
                        $i$a$-let-ComposerKt$cache$1\36\2570\35 = false;
                        if (it\35 == Composer.Companion.getEmpty()) {
                            $i$a$-cache-GroupActivity$GroupContent$detailMoreAnchorBounds$2\37\2572\0 = false;
                            value\36 = SnapshotStateKt.mutableStateOf$default(null, null, (int)2, null);
                            $this$cache\35.updateRememberedValue((Object)value\36);
                            v12 = value\36;
                        } else {
                            v12 = it\35;
                        }
                        $this$cache\32 = (MutableState)v12;
                        ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
                        detailMoreAnchorBounds$delegate = $this$cache\32;
                        ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)540973650, (String)"CC(remember):GroupActivity.kt#9igjgp");
                        invalid\35 = $composer;
                        invalid\38 = false;
                        $i$f$cache\38\327 = false;
                        it\38 = $this$cache\38.rememberedValue();
                        $i$a$-let-ComposerKt$cache$1\39\2576\38 = false;
                        if (it\38 == Composer.Companion.getEmpty()) {
                            $i$a$-cache-GroupActivity$GroupContent$selectedAlbumRecordIds$2\40\2578\0 = false;
                            value\39 = SnapshotStateKt.mutableStateOf$default((Object)SetsKt.emptySet(), null, (int)2, null);
                            $this$cache\38.updateRememberedValue((Object)value\39);
                            v13 = value\39;
                        } else {
                            v13 = it\38;
                        }
                        $this$cache\35 = (MutableState)v13;
                        ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
                        selectedAlbumRecordIds$delegate = $this$cache\35;
                        ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)540976608, (String)"CC(remember):GroupActivity.kt#9igjgp");
                        invalid\38 = $composer;
                        invalid\41 = false;
                        $i$f$cache\41\328 = false;
                        it\41 = $this$cache\41.rememberedValue();
                        $i$a$-let-ComposerKt$cache$1\42\2582\41 = false;
                        if (it\41 == Composer.Companion.getEmpty()) {
                            $i$a$-cache-GroupActivity$GroupContent$albumSelectionModeActive$2\43\2584\0 = false;
                            value\42 = SnapshotStateKt.mutableStateOf$default((Object)false, null, (int)2, null);
                            $this$cache\41.updateRememberedValue((Object)value\42);
                            v14 = value\42;
                        } else {
                            v14 = it\41;
                        }
                        $this$cache\38 = (MutableState)v14;
                        ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
                        albumSelectionModeActive$delegate = $this$cache\38;
                        ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)540979072, (String)"CC(remember):GroupActivity.kt#9igjgp");
                        invalid\41 = $composer;
                        invalid\44 = false;
                        $i$f$cache\44\329 = false;
                        it\44 = $this$cache\44.rememberedValue();
                        $i$a$-let-ComposerKt$cache$1\45\2588\44 = false;
                        if (it\44 == Composer.Companion.getEmpty()) {
                            $i$a$-cache-GroupActivity$GroupContent$showRemoveFromAlbumConfirm$2\46\2590\0 = false;
                            value\45 = SnapshotStateKt.mutableStateOf$default((Object)false, null, (int)2, null);
                            $this$cache\44.updateRememberedValue((Object)value\45);
                            v15 = value\45;
                        } else {
                            v15 = it\44;
                        }
                        $this$cache\41 = (MutableState)v15;
                        ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
                        showRemoveFromAlbumConfirm$delegate = $this$cache\41;
                        ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)540981504, (String)"CC(remember):GroupActivity.kt#9igjgp");
                        invalid\44 = $composer;
                        invalid\47 = false;
                        $i$f$cache\47\330 = false;
                        it\47 = $this$cache\47.rememberedValue();
                        $i$a$-let-ComposerKt$cache$1\48\2594\47 = false;
                        if (it\47 == Composer.Companion.getEmpty()) {
                            $i$a$-cache-GroupActivity$GroupContent$showDeleteSelectedConfirm$2\49\2596\0 = false;
                            value\48 = SnapshotStateKt.mutableStateOf$default((Object)false, null, (int)2, null);
                            $this$cache\47.updateRememberedValue((Object)value\48);
                            v16 = value\48;
                        } else {
                            v16 = it\47;
                        }
                        $this$cache\44 = (MutableState)v16;
                        ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
                        showDeleteSelectedConfirm$delegate = $this$cache\44;
                        ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)540983744, (String)"CC(remember):GroupActivity.kt#9igjgp");
                        invalid\47 = $composer;
                        invalid\50 = false;
                        $i$f$cache\50\331 = false;
                        it\50 = $this$cache\50.rememberedValue();
                        $i$a$-let-ComposerKt$cache$1\51\2600\50 = false;
                        if (it\50 == Composer.Companion.getEmpty()) {
                            $i$a$-cache-GroupActivity$GroupContent$showEditAlbumDialog$2\52\2602\0 = false;
                            value\51 = SnapshotStateKt.mutableStateOf$default((Object)false, null, (int)2, null);
                            $this$cache\50.updateRememberedValue((Object)value\51);
                            v17 = value\51;
                        } else {
                            v17 = it\50;
                        }
                        $this$cache\47 = (MutableState)v17;
                        ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
                        showEditAlbumDialog$delegate = $this$cache\47;
                        ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)540986080, (String)"CC(remember):GroupActivity.kt#9igjgp");
                        invalid\50 = $composer;
                        invalid\53 = false;
                        $i$f$cache\53\332 = false;
                        it\53 = $this$cache\53.rememberedValue();
                        $i$a$-let-ComposerKt$cache$1\54\2606\53 = false;
                        if (it\53 == Composer.Companion.getEmpty()) {
                            $i$a$-cache-GroupActivity$GroupContent$showDeleteAlbumConfirm$2\55\2608\0 = false;
                            value\54 = SnapshotStateKt.mutableStateOf$default((Object)false, null, (int)2, null);
                            $this$cache\53.updateRememberedValue((Object)value\54);
                            v18 = value\54;
                        } else {
                            v18 = it\53;
                        }
                        $this$cache\50 = (MutableState)v18;
                        ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
                        showDeleteAlbumConfirm$delegate = $this$cache\50;
                        ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)540988448, (String)"CC(remember):GroupActivity.kt#9igjgp");
                        invalid\53 = $composer;
                        invalid\56 = false;
                        $i$f$cache\56\333 = false;
                        it\56 = $this$cache\56.rememberedValue();
                        $i$a$-let-ComposerKt$cache$1\57\2612\56 = false;
                        if (it\56 == Composer.Companion.getEmpty()) {
                            $i$a$-cache-GroupActivity$GroupContent$closingStandaloneDetail$2\58\2614\0 = false;
                            value\57 = SnapshotStateKt.mutableStateOf$default((Object)false, null, (int)2, null);
                            $this$cache\56.updateRememberedValue((Object)value\57);
                            v19 = value\57;
                        } else {
                            v19 = it\56;
                        }
                        $this$cache\53 = (MutableState)v19;
                        ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
                        closingStandaloneDetail$delegate = $this$cache\53;
                        ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)540990536, (String)"CC(remember):GroupActivity.kt#9igjgp");
                        invalid\56 = $composer;
                        invalid\59 = false;
                        $i$f$cache\59\334 = false;
                        it\59 = $this$cache\59.rememberedValue();
                        $i$a$-let-ComposerKt$cache$1\60\2618\59 = false;
                        if (it\59 == Composer.Companion.getEmpty()) {
                            $i$a$-cache-GroupActivity$GroupContent$editingAlbumId$2\61\2620\0 = false;
                            value\60 = SnapshotStateKt.mutableStateOf$default(null, null, (int)2, null);
                            $this$cache\59.updateRememberedValue((Object)value\60);
                            v20 = value\60;
                        } else {
                            v20 = it\59;
                        }
                        $this$cache\56 = (MutableState)v20;
                        ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
                        editingAlbumId$delegate = $this$cache\56;
                        ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)540992861, (String)"CC(remember):GroupActivity.kt#9igjgp");
                        invalid\59 = $composer;
                        invalid\62 = false;
                        $i$f$cache\62\335 = false;
                        it\62 = $this$cache\62.rememberedValue();
                        $i$a$-let-ComposerKt$cache$1\63\2624\62 = false;
                        if (it\62 == Composer.Companion.getEmpty()) {
                            $i$a$-cache-GroupActivity$GroupContent$albumNameInput$2\64\2626\0 = false;
                            value\63 = SnapshotStateKt.mutableStateOf$default((Object)"", null, (int)2, null);
                            $this$cache\62.updateRememberedValue((Object)value\63);
                            v21 = value\63;
                        } else {
                            v21 = it\62;
                        }
                        $this$cache\59 = (MutableState)v21;
                        ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
                        albumNameInput$delegate = $this$cache\59;
                        ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)540995069, (String)"CC(remember):GroupActivity.kt#9igjgp");
                        invalid\62 = $composer;
                        invalid\65 = false;
                        $i$f$cache\65\336 = false;
                        it\65 = $this$cache\65.rememberedValue();
                        $i$a$-let-ComposerKt$cache$1\66\2630\65 = false;
                        if (it\65 == Composer.Companion.getEmpty()) {
                            $i$a$-cache-GroupActivity$GroupContent$albumDescriptionInput$2\67\2632\0 = false;
                            value\66 = SnapshotStateKt.mutableStateOf$default((Object)"", null, (int)2, null);
                            $this$cache\65.updateRememberedValue((Object)value\66);
                            v22 = value\66;
                        } else {
                            v22 = it\65;
                        }
                        $this$cache\62 = (MutableState)v22;
                        ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
                        albumDescriptionInput$delegate = $this$cache\62;
                        ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)540997536, (String)"CC(remember):GroupActivity.kt#9igjgp");
                        invalid\65 = $composer;
                        invalid\68 = false;
                        $i$f$cache\68\337 = false;
                        it\68 = $this$cache\68.rememberedValue();
                        $i$a$-let-ComposerKt$cache$1\69\2636\68 = false;
                        if (it\68 == Composer.Companion.getEmpty()) {
                            $i$a$-cache-GroupActivity$GroupContent$albumAutoClassifyEnabledInput$2\70\2638\0 = false;
                            value\69 = SnapshotStateKt.mutableStateOf$default((Object)false, null, (int)2, null);
                            $this$cache\68.updateRememberedValue(value\69);
                            v23 = value\69;
                        } else {
                            v23 = it\68;
                        }
                        $this$cache\65 = (MutableState)v23;
                        ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
                        albumAutoClassifyEnabledInput$delegate = $this$cache\65;
                        ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)540999613, (String)"CC(remember):GroupActivity.kt#9igjgp");
                        invalid\68 = $composer;
                        invalid\71 = $composer.changed(allRecords);
                        $i$f$cache\71\338 = false;
                        it\71 = $this$cache\71.rememberedValue();
                        $i$a$-let-ComposerKt$cache$1\72\2642\71 = false;
                        if (invalid\71 || it\71 == Composer.Companion.getEmpty()) {
                            $i$a$-cache-GroupActivity$GroupContent$validRecordIds$1\73\2644\0 = false;
                            $this$map\74 = allRecords;
                            $i$f$map\74\338 = false;
                            var66_190 = $this$map\74;
                            destination\75 = new ArrayList<E>(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map\74, (int)10));
                            $i$f$mapTo\75\2645 = false;
                            for (T item\75 : $this$mapTo\75) {
                                var71_223 = (MemoryRecord)item\75;
                                var72_230 = destination\75;
                                $i$a$-map-GroupActivity$GroupContent$validRecordIds$1$1\76\2647\73 = false;
                                var72_230.add(it\76.getRecordId());
                            }
                            value\72 = CollectionsKt.toSet((Iterable)((List)destination\75));
                            $this$cache\71.updateRememberedValue((Object)value\72);
                            v24 = value\72;
                        } else {
                            v24 = it\71;
                        }
                        $this$cache\68 = (Set)v24;
                        ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
                        validRecordIds = $this$cache\68;
                        v25 = albumRefreshTick;
                        ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)541002969, (String)"CC(remember):GroupActivity.kt#9igjgp");
                        $this$cache\71 = $composer;
                        invalid\77 = $composer.changedInstance((Object)albumStore);
                        $i$f$cache\77\339 = false;
                        it\77 = $this$cache\77.rememberedValue();
                        $i$a$-let-ComposerKt$cache$1\78\2652\77 = false;
                        if (invalid\77 || it\77 == Composer.Companion.getEmpty()) {
                            var97_255 = v25;
                            $i$a$-cache-GroupActivity$GroupContent$1\79\2654\0 = false;
                            var98_261 /* !! */  = (Function2)new Function2<CoroutineScope, Continuation<? super Unit>, Object>(albumStore, (MutableState<List<GroupAlbumStore.GroupAlbum>>)albumList$delegate, null){
                                int label;
                                final /* synthetic */ GroupAlbumStore $albumStore;
                                final /* synthetic */ MutableState<List<GroupAlbumStore.GroupAlbum>> $albumList$delegate;
                                {
                                    this.$albumStore = $albumStore;
                                    this.$albumList$delegate = $albumList$delegate;
                                    super(2, $completion);
                                }

                                public final Object invokeSuspend(Object $result) {
                                    IntrinsicsKt.getCOROUTINE_SUSPENDED();
                                    switch (this.label) {
                                        case 0: {
                                            ResultKt.throwOnFailure((Object)$result);
                                            GroupActivity.access$GroupContent$lambda$35(this.$albumList$delegate, this.$albumStore.loadAlbums());
                                            return Unit.INSTANCE;
                                        }
                                    }
                                    throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                                }

                                public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
                                    return (Continuation)new /* invalid duplicate definition of identical inner class */;
                                }

                                public final Object invoke(CoroutineScope p1, Continuation<? super Unit> p2) {
                                    return (this.create(p1, p2)).invokeSuspend(Unit.INSTANCE);
                                }
                            };
                            v25 = var97_255;
                            value\78 = var98_261 /* !! */ ;
                            $this$cache\77.updateRememberedValue((Object)value\78);
                            v26 = value\78;
                        } else {
                            v26 = it\77;
                        }
                        $this$cache\68 = (Function2)v26;
                        ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
                        EffectsKt.LaunchedEffect((Object)v25, (Function2)$this$cache\68, (Composer)$composer, (int)(14 & $dirty1 >> 15));
                        albumColumns = albumAdaptive.getWidthClass() == NoMemoWidthClass.EXPANDED ? 3 : 2;
                        filteredAlbumList = GroupActivity.GroupContent$lambda$34((MutableState<List<GroupAlbumStore.GroupAlbum>>)albumList$delegate);
                        ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)541010099, (String)"CC(remember):GroupActivity.kt#9igjgp");
                        it\77 = $composer;
                        invalid\80 = $composer.changed(filteredAlbumList) | $composer.changed(albumColumns);
                        $i$f$cache\80\345 = false;
                        it\80 = $this$cache\80.rememberedValue();
                        $i$a$-let-ComposerKt$cache$1\81\2658\80 = false;
                        if (invalid\80 || it\80 == Composer.Companion.getEmpty()) {
                            $i$a$-cache-GroupActivity$GroupContent$albumRows$1\82\2660\0 = false;
                            value\81 = CollectionsKt.chunked((Iterable)filteredAlbumList, (int)albumColumns);
                            $this$cache\80.updateRememberedValue(value\81);
                            v27 = value\81;
                        } else {
                            v27 = it\80;
                        }
                        $i$f$cache\77\339 = (List)v27;
                        ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
                        albumRows = $i$f$cache\77\339;
                        ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)541013629, (String)"CC(remember):GroupActivity.kt#9igjgp");
                        invalid\80 = $composer;
                        invalid\83 = $composer.changed(allRecords);
                        $i$f$cache\83\346 = false;
                        it\83 = $this$cache\83.rememberedValue();
                        $i$a$-let-ComposerKt$cache$1\84\2664\83 = false;
                        if (invalid\83 || it\83 == Composer.Companion.getEmpty()) {
                            $i$a$-cache-GroupActivity$GroupContent$recordById$1\85\2666\0 = false;
                            $this$associateBy\86 = allRecords;
                            $i$f$associateBy\86\346 = false;
                            capacity\86 = RangesKt.coerceAtLeast((int)MapsKt.mapCapacity((int)CollectionsKt.collectionSizeOrDefault((Iterable)$this$associateBy\86, (int)10)), (int)16);
                            it\76 = $this$associateBy\86;
                            destination\87 = new LinkedHashMap<K, V>(capacity\86);
                            $i$f$associateByTo\87\2668 = false;
                            for (Object element\87 : $this$associateByTo\87) {
                                var76_268 = (MemoryRecord)element\87;
                                var77_274 = destination\87;
                                $i$a$-associateBy-GroupActivity$GroupContent$recordById$1$1\88\2670\85 = false;
                                var77_274.put(it\88.getRecordId(), element\87);
                            }
                            value\84 = destination\87;
                            $this$cache\83.updateRememberedValue(value\84);
                            v28 = value\84;
                        } else {
                            v28 = it\83;
                        }
                        $this$cache\80 = (Map)v28;
                        ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
                        recordById = $this$cache\80;
                        ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)541017043, (String)"CC(remember):GroupActivity.kt#9igjgp");
                        invalid\83 = $composer;
                        invalid\89 = $composer.changed(filteredAlbumList) | $composer.changed((Object)recordById);
                        $i$f$cache\89\347 = false;
                        it\89 = $this$cache\89.rememberedValue();
                        $i$a$-let-ComposerKt$cache$1\90\2676\89 = false;
                        if (invalid\89 || it\89 == Composer.Companion.getEmpty()) {
                            $i$a$-cache-GroupActivity$GroupContent$albumPreviewRecordsMap$1\91\2678\0 = false;
                            $this$associate\92 = filteredAlbumList;
                            $i$f$associate\92\348 = false;
                            capacity\92 = RangesKt.coerceAtLeast((int)MapsKt.mapCapacity((int)CollectionsKt.collectionSizeOrDefault((Iterable)$this$associate\92, (int)10)), (int)16);
                            destination\87 = $this$associate\92;
                            destination\93 = new LinkedHashMap<K, V>(capacity\92);
                            $i$f$associateTo\93\2680 = false;
                            for (E element\93 : $this$associateTo\93) {
                                var77_274 = destination\93;
                                album\94 = (GroupAlbumStore.GroupAlbum)element\93;
                                $i$a$-associate-GroupActivity$GroupContent$albumPreviewRecordsMap$1$1\94\2682\91 = false;
                                var80_302 = album\94.getRecordIds();
                                var81_310 = album\94.getAlbumId();
                                $i$f$mapNotNull\95\350 = false;
                                var83_321 = $this$mapNotNull\95;
                                destination\96 = new ArrayList<E>();
                                $i$f$mapNotNullTo\96\2683 = false;
                                $this$forEach\97 = $this$mapNotNullTo\96;
                                $i$f$forEach\97\2692 = false;
                                var88_360 = $this$forEach\97.iterator();
                                while (var88_360.hasNext()) {
                                    element\98 = element\97 = var88_360.next();
                                    $i$a$-forEach-CollectionsKt___CollectionsKt$mapNotNullTo$1\98\2693\96 = false;
                                    it\99 = (String)element\98;
                                    $i$a$-mapNotNull-GroupActivity$GroupContent$albumPreviewRecordsMap$1$1$1\99\2692\94 = false;
                                    if ((MemoryRecord)recordById.get(it\99) == null) continue;
                                    $i$a$-let-CollectionsKt___CollectionsKt$mapNotNullTo$1$1\100\2694\98 = false;
                                    destination\96.add(it\98);
                                }
                                album\94 = TuplesKt.to((Object)var81_310, (Object)CollectionsKt.take((Iterable)((List)destination\96), (int)3));
                                var77_274.put(album\94.getFirst(), album\94.getSecond());
                            }
                            value\90 = destination\93;
                            $this$cache\89.updateRememberedValue(value\90);
                            v29 = value\90;
                        } else {
                            v29 = it\89;
                        }
                        $this$cache\83 = (Map)v29;
                        ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
                        albumPreviewRecordsMap = $this$cache\83;
                        $this$cache\89 = GroupActivity.GroupContent$lambda$34((MutableState<List<GroupAlbumStore.GroupAlbum>>)albumList$delegate);
                        invalid\89 = GroupActivity.GroupContent$lambda$37((MutableState<String>)openedAlbumId$delegate);
                        ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)541025614, (String)"CC(remember):GroupActivity.kt#9igjgp");
                        it\89 = $composer;
                        invalid\101 = $composer.changed($this$cache\89) | $composer.changed(invalid\89);
                        $i$f$cache\101\354 = false;
                        it\101 = $this$cache\101.rememberedValue();
                        $i$a$-let-ComposerKt$cache$1\102\2701\101 = false;
                        if (invalid\101 || it\101 == Composer.Companion.getEmpty()) {
                            block131: {
                                $i$a$-cache-GroupActivity$GroupContent$openedAlbum$1\103\2703\0 = false;
                                $this$firstOrNull\104 = GroupActivity.GroupContent$lambda$34((MutableState<List<GroupAlbumStore.GroupAlbum>>)albumList$delegate);
                                $i$f$firstOrNull\104\355 = false;
                                for (Object element\104 : $this$firstOrNull\104) {
                                    it\105 = (GroupAlbumStore.GroupAlbum)element\104;
                                    $i$a$-firstOrNull-GroupActivity$GroupContent$openedAlbum$1$1\105\2704\103 = false;
                                    if (!Intrinsics.areEqual((Object)it\105.getAlbumId(), (Object)GroupActivity.GroupContent$lambda$37((MutableState<String>)openedAlbumId$delegate))) continue;
                                    v30 = element\104;
                                    break block131;
                                }
                                v30 = null;
                            }
                            value\102 = v30;
                            $this$cache\101.updateRememberedValue(value\102);
                            v31 = value\102;
                        } else {
                            v31 = it\101;
                        }
                        $i$f$cache\89\347 = (GroupAlbumStore.GroupAlbum)v31;
                        ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
                        openedAlbum = $i$f$cache\89\347;
                        invalid\89 = GroupActivity.GroupContent$lambda$34((MutableState<List<GroupAlbumStore.GroupAlbum>>)albumList$delegate);
                        $i$f$cache\89\347 = GroupActivity.GroupContent$lambda$37((MutableState<String>)openedAlbumId$delegate);
                        ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)541030480, (String)"CC(remember):GroupActivity.kt#9igjgp");
                        invalid\101 = $composer;
                        invalid\106 = $composer.changed(invalid\89) | $composer.changed((Object)$i$f$cache\89\347);
                        $i$f$cache\106\357 = false;
                        it\106 = $this$cache\106.rememberedValue();
                        $i$a$-let-ComposerKt$cache$1\107\2709\106 = false;
                        if (invalid\106 || it\106 == Composer.Companion.getEmpty()) {
                            block132: {
                                $i$a$-cache-GroupActivity$GroupContent$currentAlbumRecordIds$1\108\2711\0 = false;
                                $i$f$firstOrNull\104\355 = GroupActivity.GroupContent$lambda$34((MutableState<List<GroupAlbumStore.GroupAlbum>>)albumList$delegate);
                                $i$f$firstOrNull\109\359 = false;
                                for (E element\109 : $this$firstOrNull\109) {
                                    it\110 = (GroupAlbumStore.GroupAlbum)element\109;
                                    $i$a$-firstOrNull-GroupActivity$GroupContent$currentAlbumRecordIds$1$1\110\2712\108 = false;
                                    if (!Intrinsics.areEqual((Object)it\110.getAlbumId(), (Object)GroupActivity.GroupContent$lambda$37((MutableState<String>)openedAlbumId$delegate))) continue;
                                    v32 = element\109;
                                    break block132;
                                }
                                v32 = null;
                            }
                            $i$a$-associate-GroupActivity$GroupContent$albumPreviewRecordsMap$1$1\94\2682\91 = v32;
                            v33 = $i$a$-associate-GroupActivity$GroupContent$albumPreviewRecordsMap$1$1\94\2682\91 != null && ($this$firstOrNull\109 = $i$a$-associate-GroupActivity$GroupContent$albumPreviewRecordsMap$1$1\94\2682\91.getRecordIds()) != null ? CollectionsKt.toSet((Iterable)$this$firstOrNull\109) : null;
                            if (v33 == null) {
                                v33 = SetsKt.emptySet();
                            }
                            value\107 = v33;
                            $this$cache\106.updateRememberedValue((Object)value\107);
                            v34 = value\107;
                        } else {
                            v34 = it\106;
                        }
                        $this$cache\101 = (Set)v34;
                        ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
                        currentAlbumRecordIds = $this$cache\101;
                        v35 = openedAlbum;
                        $i$f$cache\89\347 = v35 != null ? v35.getRecordIds() : null;
                        ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)541038159, (String)"CC(remember):GroupActivity.kt#9igjgp");
                        $this$cache\106 = $composer;
                        invalid\111 = $composer.changed(allRecords) | $composer.changed($i$f$cache\89\347);
                        $i$f$cache\111\364 = false;
                        it\111 = $this$cache\111.rememberedValue();
                        $i$a$-let-ComposerKt$cache$1\112\2717\111 = false;
                        if (invalid\111 || it\111 == Composer.Companion.getEmpty()) {
                            $i$a$-cache-GroupActivity$GroupContent$openedRecords$1\113\2719\0 = false;
                            v36 = openedAlbum;
                            if (v36 == null) {
                                v37 = CollectionsKt.emptyList();
                            } else {
                                current\113 = v36;
                                $this$associateBy\114 = allRecords;
                                $i$f$associateBy\114\366 = false;
                                capacity\114 = RangesKt.coerceAtLeast((int)MapsKt.mapCapacity((int)CollectionsKt.collectionSizeOrDefault((Iterable)$this$associateBy\114, (int)10)), (int)16);
                                it\110 = $this$associateBy\114;
                                destination\115 = new LinkedHashMap<K, V>(capacity\114);
                                $i$f$associateByTo\115\2721 = false;
                                for (Object element\115 : $this$associateByTo\115) {
                                    $i$f$mapNotNull\95\350 = (MemoryRecord)element\115;
                                    $this$mapNotNullTo\96 = destination\115;
                                    $i$a$-associateBy-GroupActivity$GroupContent$openedRecords$1$byId$1\116\2723\113 = false;
                                    $this$mapNotNullTo\96.put(it\116.getRecordId(), element\115);
                                }
                                byId\113 = destination\115;
                                $this$mapNotNull\117 = current\113.getRecordIds();
                                $i$f$mapNotNull\117\367 = false;
                                capacity\114 = $this$mapNotNull\117;
                                destination\118 = new ArrayList<E>();
                                $i$f$mapNotNullTo\118\2726 = false;
                                $this$forEach\119 = $this$mapNotNullTo\118;
                                $i$f$forEach\119\2735 = false;
                                element\115 = $this$forEach\119.iterator();
                                while (element\115.hasNext()) {
                                    element\120 = element\119 = element\115.next();
                                    $i$a$-forEach-CollectionsKt___CollectionsKt$mapNotNullTo$1\120\2736\118 = false;
                                    it\121 = (String)element\120;
                                    $i$a$-mapNotNull-GroupActivity$GroupContent$openedRecords$1$1\121\2735\113 = false;
                                    if ((MemoryRecord)byId\113.get(it\121) == null) continue;
                                    $i$a$-let-CollectionsKt___CollectionsKt$mapNotNullTo$1$1\122\2737\120 = false;
                                    destination\118.add(it\120);
                                }
                                v37 = (List)destination\118;
                            }
                            value\112 = v37;
                            $this$cache\111.updateRememberedValue(value\112);
                            v38 = value\112;
                        } else {
                            v38 = it\111;
                        }
                        $this$cache\101 = (List)v38;
                        ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
                        openedRecords /* !! */  = $this$cache\101;
                        $this$cache\101 = GroupActivity.GroupContent$lambda$64((MutableState<Set<String>>)selectedAlbumRecordIds$delegate);
                        ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)541046923, (String)"CC(remember):GroupActivity.kt#9igjgp");
                        invalid\111 = $composer;
                        invalid\123 = $composer.changed((Object)openedRecords /* !! */ ) | $composer.changed($this$cache\101);
                        $i$f$cache\123\369 = false;
                        it\123 = $this$cache\123.rememberedValue();
                        $i$a$-let-ComposerKt$cache$1\124\2743\123 = false;
                        if (invalid\123 || it\123 == Composer.Companion.getEmpty()) {
                            $i$a$-cache-GroupActivity$GroupContent$selectedAlbumRecords$1\125\2745\0 = false;
                            $this$filter\126 = openedRecords /* !! */ ;
                            $i$f$filter\126\370 = false;
                            $this$mapNotNullTo\118 = $this$filter\126;
                            destination\127 = new ArrayList<E>();
                            $i$f$filterTo\127\2746 = false;
                            for (E element\127 : $this$filterTo\127) {
                                it\128 = (MemoryRecord)element\127;
                                $i$a$-filter-GroupActivity$GroupContent$selectedAlbumRecords$1$1\128\2747\125 = false;
                                if (!GroupActivity.GroupContent$lambda$64((MutableState<Set<String>>)selectedAlbumRecordIds$delegate).contains(it\128.getRecordId())) continue;
                                destination\127.add(element\127);
                            }
                            value\124 = (List)destination\127;
                            $this$cache\123.updateRememberedValue((Object)value\124);
                            v39 = value\124;
                        } else {
                            v39 = it\123;
                        }
                        $this$cache\111 = (List)v39;
                        ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
                        selectedAlbumRecords = $this$cache\111;
                        $this$cache\111 = openedRecords /* !! */ ;
                        if (!($this$cache\111.isEmpty() == false)) ** GOTO lbl-1000
                        $this$all\129 = openedRecords /* !! */ ;
                        $i$f$all\129\373 = false;
                        if ($this$all\129 instanceof Collection && ((Collection)$this$all\129).isEmpty()) {
                            v40 = true;
                        } else {
                            for (T element\129 : $this$all\129) {
                                it\130 = (MemoryRecord)element\129;
                                $i$a$-all-GroupActivity$GroupContent$allOpenedRecordsSelected$1\130\2753\0 = false;
                                if (GroupActivity.GroupContent$lambda$64((MutableState<Set<String>>)selectedAlbumRecordIds$delegate).contains(it\130.getRecordId())) continue;
                                v40 = false;
                                break block133;
                            }
                            v40 = true;
                        }
                    }
                    if (v40) {
                        v41 = true;
                    } else lbl-1000:
                    // 2 sources

                    {
                        v41 = false;
                    }
                    allOpenedRecordsSelected = v41;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)541057583, (String)"CC(remember):GroupActivity.kt#9igjgp");
                    invalid\123 = $composer;
                    invalid\131 = $composer.changed(allRecords) | $composer.changed((Object)currentAlbumRecordIds);
                    $i$f$cache\131\374 = false;
                    it\131 = $this$cache\131.rememberedValue();
                    $i$a$-let-ComposerKt$cache$1\132\2755\131 = false;
                    if (invalid\131 || it\131 == Composer.Companion.getEmpty()) {
                        $i$a$-cache-GroupActivity$GroupContent$availableExistingRecords$1\133\2757\0 = false;
                        $this$filterNot\134 = allRecords;
                        $i$f$filterNot\134\377 = false;
                        destination\127 = $this$filterNot\134;
                        destination\135 = new ArrayList<E>();
                        $i$f$filterNotTo\135\2758 = false;
                        for (T element\135 : $this$filterNotTo\135) {
                            it\136 = (MemoryRecord)element\135;
                            $i$a$-filterNot-GroupActivity$GroupContent$availableExistingRecords$1$1\136\2759\133 = false;
                            if (currentAlbumRecordIds.contains(it\136.getRecordId())) continue;
                            destination\135.add(element\135);
                        }
                        value\132 = (List)destination\135;
                        $this$cache\131.updateRememberedValue((Object)value\132);
                        v42 = value\132;
                    } else {
                        v42 = it\131;
                    }
                    $i$f$all\129\373 = (List)v42;
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
                    availableExistingRecords = $i$f$all\129\373;
                    $this$cache\131 = GroupActivity.GroupContent$lambda$49((MutableState<String>)addExistingSearchQuery$delegate);
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)541068053, (String)"CC(remember):GroupActivity.kt#9igjgp");
                    $i$f$cache\131\374 = $composer;
                    invalid\137 = $composer.changed((Object)availableExistingRecords) | $composer.changed((Object)$this$cache\131);
                    $i$f$cache\137\379 = false;
                    it\137 = $this$cache\137.rememberedValue();
                    $i$a$-let-ComposerKt$cache$1\138\2764\137 = 0;
                    if (invalid\137 || it\137 == Composer.Companion.getEmpty()) {
                        $i$a$-cache-GroupActivity$GroupContent$filteredExistingRecords$1\139\2766\0 = false;
                        v43 = StringsKt.trim((CharSequence)GroupActivity.GroupContent$lambda$49((MutableState<String>)addExistingSearchQuery$delegate)).toString().toLowerCase(Locale.ROOT);
                        Intrinsics.checkNotNullExpressionValue((Object)v43, (String)"toLowerCase(...)");
                        query\139 = v43;
                        if (StringsKt.isBlank((CharSequence)query\139)) {
                            v44 = availableExistingRecords;
                        } else {
                            $this$filter\140 = availableExistingRecords;
                            $i$f$filter\140\384 = false;
                            element\127 = $this$filter\140;
                            destination\141 = new ArrayList<E>();
                            $i$f$filterTo\141\2767 = false;
                            for (T element\141 : $this$filterTo\141) {
                                record\142 = (MemoryRecord)element\141;
                                $i$a$-filter-GroupActivity$GroupContent$filteredExistingRecords$1$1\142\2768\139 = false;
                                it\121 = new String[]{record\142.getTitle(), record\142.getSummary(), record\142.getMemory(), record\142.getSourceText(), record\142.getAnalysis(), record\142.getCategoryName()};
                                v45 = CollectionsKt.joinToString$default((Iterable)CollectionsKt.listOf((Object[])it\121), (CharSequence)"\n", null, null, (int)0, null, (Function1)(Function1)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)Ljava/lang/Object;, GroupContent$lambda$119$lambda$118$lambda$117(java.lang.String ), (Ljava/lang/String;)Ljava/lang/CharSequence;)(), (int)30, null).toLowerCase(Locale.ROOT);
                                Intrinsics.checkNotNullExpressionValue((Object)v45, (String)"toLowerCase(...)");
                                if (!StringsKt.contains$default((CharSequence)v45, (CharSequence)query\139, (boolean)false, (int)2, null)) continue;
                                destination\141.add(element\141);
                            }
                            v44 = (List)destination\141;
                        }
                        value\138 = v44;
                        $this$cache\137.updateRememberedValue((Object)value\138);
                        v46 = value\138;
                    } else {
                        v46 = it\137;
                    }
                    invalid\131 = (List)v46;
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
                    filteredExistingRecords = invalid\131;
                    invalid\131 = (CompositionLocal)CompositionLocalsKt.getLocalDensity();
                    $this$cache\137 = $composer;
                    $changed\143 = false;
                    $i$f$getCurrent\143\399 = false;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer\143, (int)2023513938, (String)"CC(<get-current>):CompositionLocal.kt#9igjgp");
                    it\137 = $composer\143.consume((CompositionLocal)this_\143);
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer\143);
                    $this$GroupContent_u24lambda_u24120\144 = density = (Density)it\137;
                    $i$a$-with-GroupActivity$GroupContent$groupHeaderCollapseDistancePx$1\144\400\0 = false;
                    $this$dp\145 = 84 != 0;
                    $i$f$getDp\145\400 = 0;
                    groupHeaderCollapseDistancePx = $this$GroupContent_u24lambda_u24120\144.toPx-0680j_4(Dp.constructor-impl((float)((float)$this$dp\145)));
                    v47 = openedAlbum;
                    $i$a$-with-GroupActivity$GroupContent$groupHeaderCollapseDistancePx$1\144\400\0 = v47 != null ? v47.getAlbumId() : null;
                    $this$dp\145 = GroupActivity.GroupContent$lambda$34((MutableState<List<GroupAlbumStore.GroupAlbum>>)albumList$delegate).isEmpty();
                    $i$f$getDp\145\400 = groupListState.getFirstVisibleItemIndex();
                    $i$a$-let-ComposerKt$cache$1\138\2764\137 = groupListState.getFirstVisibleItemScrollOffset();
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)541097423, (String)"CC(remember):GroupActivity.kt#9igjgp");
                    query\139 = $composer;
                    invalid\146 = $composer.changed((Object)$i$a$-with-GroupActivity$GroupContent$groupHeaderCollapseDistancePx$1\144\400\0) | $composer.changed($this$dp\145) | $composer.changed($i$f$getDp\145\400) | $composer.changed($i$a$-let-ComposerKt$cache$1\138\2764\137);
                    $i$f$cache\146\401 = false;
                    it\146 = $this$cache\146.rememberedValue();
                    $i$a$-let-ComposerKt$cache$1\147\2776\146 = false;
                    if (invalid\146 || it\146 == Composer.Companion.getEmpty()) {
                        $i$a$-cache-GroupActivity$GroupContent$groupHeaderCollapseTarget$2\148\2778\0 = false;
                        value\147 = SnapshotStateKt.derivedStateOf((Function0)(Function0)LambdaMetafactory.metafactory(null, null, null, ()Ljava/lang/Object;, GroupContent$lambda$122$lambda$121(com.han.nomemo.GroupAlbumStore$GroupAlbum androidx.compose.foundation.lazy.LazyListState float androidx.compose.runtime.MutableState ), ()Ljava/lang/Float;)((GroupAlbumStore.GroupAlbum)openedAlbum, (LazyListState)groupListState, (float)groupHeaderCollapseDistancePx, (MutableState)albumList$delegate));
                        $this$cache\146.updateRememberedValue((Object)value\147);
                        v48 = value\147;
                    } else {
                        v48 = it\146;
                    }
                    $i$a$-cache-GroupActivity$GroupContent$filteredExistingRecords$1\139\2766\0 = (State)v48;
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
                    groupHeaderCollapseTarget$delegate = $i$a$-cache-GroupActivity$GroupContent$filteredExistingRecords$1\139\2766\0;
                    groupHeaderCollapseProgress$delegate = AnimateAsStateKt.animateFloatAsState((float)GroupActivity.GroupContent$lambda$123((State<Float>)groupHeaderCollapseTarget$delegate), (AnimationSpec)((AnimationSpec)AnimationSpecKt.tween$default((int)180, (int)0, (Easing)EasingKt.getFastOutSlowInEasing(), (int)2, null)), (float)0.0f, (String)"groupHeaderCollapse", null, (Composer)$composer, (int)3072, (int)20);
                    groupExpandedTitleAlpha = RangesKt.coerceIn((float)(1.0f - GroupActivity.GroupContent$lambda$124((State<Float>)groupHeaderCollapseProgress$delegate) / 0.42f), (float)0.0f, (float)1.0f);
                    groupCollapsedTitleAlpha = RangesKt.coerceIn((float)((GroupActivity.GroupContent$lambda$124((State<Float>)groupHeaderCollapseProgress$delegate) - 0.74f) / 0.22f), (float)0.0f, (float)1.0f);
                    $this$GroupContent_u24lambda_u24125\149 = density;
                    $i$a$-with-GroupActivity$GroupContent$groupExpandedTitleTranslateY$1\149\428\0 = false;
                    $this$dp\150 = -20;
                    $i$f$getDp\150\428 = false;
                    groupExpandedTitleTranslateY = $this$GroupContent_u24lambda_u24125\149.toPx-0680j_4(Dp.constructor-impl((float)$this$dp\150)) * GroupActivity.GroupContent$lambda$124((State<Float>)groupHeaderCollapseProgress$delegate);
                    if (albumAdaptive.isNarrow()) {
                        $this$dp\151 = 44;
                        $i$f$getDp\151\429 = false;
                        v49 = Dp.constructor-impl((float)$this$dp\151);
                    } else {
                        $this$dp\152 = 50;
                        $i$f$getDp\152\429 = false;
                        v49 = Dp.constructor-impl((float)$this$dp\152);
                    }
                    groupExpandedTitleMaxHeight = v49;
                    $this$dp\153 = false;
                    $i$f$getDp\153\431 = false;
                    groupExpandedTitleHeight$delegate = AnimateAsStateKt.animateDpAsState-AjpBEmI((float)DpKt.lerp-Md-fbLM((float)groupExpandedTitleMaxHeight, (float)Dp.constructor-impl((float)((float)$this$dp\153)), (float)GroupActivity.GroupContent$lambda$124((State<Float>)groupHeaderCollapseProgress$delegate)), (AnimationSpec)((AnimationSpec)AnimationSpecKt.tween$default((int)180, (int)0, (Easing)EasingKt.getFastOutSlowInEasing(), (int)2, null)), (String)"groupExpandedTitleHeight", null, (Composer)$composer, (int)384, (int)8);
                    $this$dp\154 = 14;
                    $i$f$getDp\154\435 = false;
                    groupListSpacing = Dp.constructor-impl((float)$this$dp\154);
                    $this$dp\155 = 12;
                    $i$f$getDp\155\437 = false;
                    $this$dp\156 = 4;
                    $i$f$getDp\156\437 = false;
                    groupListTopPadding$delegate = AnimateAsStateKt.animateDpAsState-AjpBEmI((float)DpKt.lerp-Md-fbLM((float)Dp.constructor-impl((float)$this$dp\155), (float)Dp.constructor-impl((float)$this$dp\156), (float)GroupActivity.GroupContent$lambda$124((State<Float>)groupHeaderCollapseProgress$delegate)), (AnimationSpec)((AnimationSpec)AnimationSpecKt.tween$default((int)180, (int)0, (Easing)EasingKt.getFastOutSlowInEasing(), (int)2, null)), (String)"groupListTopPadding", null, (Composer)$composer, (int)384, (int)8);
                    v50 /* !! */  = hasLoadedRecords;
                    v51 = validRecordIds;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)541162408, (String)"CC(remember):GroupActivity.kt#9igjgp");
                    $i$f$getDp\156\437 = $composer;
                    invalid\157 = ($dirty & 112) == 32 | $composer.changedInstance((Object)albumStore) | $composer.changedInstance((Object)validRecordIds);
                    $i$f$cache\157\441 = false;
                    it\157 = $this$cache\157.rememberedValue();
                    $i$a$-let-ComposerKt$cache$1\158\2787\157 = false;
                    if (invalid\157 || it\157 == Composer.Companion.getEmpty()) {
                        var98_261 /* !! */  = v51;
                        var97_255 = v50 /* !! */ ;
                        $i$a$-cache-GroupActivity$GroupContent$2\159\2789\0 = false;
                        var99_373 /* !! */  = (Function2)new Function2<CoroutineScope, Continuation<? super Unit>, Object>(hasLoadedRecords, albumStore, (Set<String>)validRecordIds, (MutableState<List<GroupAlbumStore.GroupAlbum>>)albumList$delegate, null){
                            int label;
                            final /* synthetic */ boolean $hasLoadedRecords;
                            final /* synthetic */ GroupAlbumStore $albumStore;
                            final /* synthetic */ Set<String> $validRecordIds;
                            final /* synthetic */ MutableState<List<GroupAlbumStore.GroupAlbum>> $albumList$delegate;
                            {
                                this.$hasLoadedRecords = $hasLoadedRecords;
                                this.$albumStore = $albumStore;
                                this.$validRecordIds = $validRecordIds;
                                this.$albumList$delegate = $albumList$delegate;
                                super(2, $completion);
                            }

                            public final Object invokeSuspend(Object $result) {
                                IntrinsicsKt.getCOROUTINE_SUSPENDED();
                                switch (this.label) {
                                    case 0: {
                                        ResultKt.throwOnFailure((Object)$result);
                                        if (!this.$hasLoadedRecords) {
                                            return Unit.INSTANCE;
                                        }
                                        if (this.$albumStore.pruneInvalidRecordIds(this.$validRecordIds)) {
                                            GroupActivity.access$GroupContent$lambda$35(this.$albumList$delegate, this.$albumStore.loadAlbums());
                                        }
                                        return Unit.INSTANCE;
                                    }
                                }
                                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                            }

                            public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
                                return (Continuation)new /* invalid duplicate definition of identical inner class */;
                            }

                            public final Object invoke(CoroutineScope p1, Continuation<? super Unit> p2) {
                                return (this.create(p1, p2)).invokeSuspend(Unit.INSTANCE);
                            }
                        };
                        v50 /* !! */  = var97_255;
                        v51 = var98_261 /* !! */ ;
                        value\158 = var99_373 /* !! */ ;
                        $this$cache\157.updateRememberedValue((Object)value\158);
                        v52 = value\158;
                    } else {
                        v52 = it\157;
                    }
                    var80_309 = (Function2)v52;
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
                    EffectsKt.LaunchedEffect((Object)v50 /* !! */ , (Object)v51, (Function2)var80_309, (Composer)$composer, (int)(14 & $dirty >> 3));
                    v53 = GroupActivity.GroupContent$lambda$37((MutableState<String>)openedAlbumId$delegate);
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)541171107, (String)"CC(remember):GroupActivity.kt#9igjgp");
                    $this$cache\157 = $composer;
                    invalid\160 = false;
                    $i$f$cache\160\449 = false;
                    it\160 = $this$cache\160.rememberedValue();
                    $i$a$-let-ComposerKt$cache$1\161\2793\160 = false;
                    if (it\160 == Composer.Companion.getEmpty()) {
                        var97_255 = v53;
                        $i$a$-cache-GroupActivity$GroupContent$3\162\2795\0 = false;
                        var98_261 /* !! */  = (Function2)new Function2<CoroutineScope, Continuation<? super Unit>, Object>((MutableState<Set<String>>)selectedAlbumRecordIds$delegate, (MutableState<Boolean>)albumSelectionModeActive$delegate, (MutableState<Boolean>)showRemoveFromAlbumConfirm$delegate, (MutableState<Boolean>)showDeleteSelectedConfirm$delegate, null){
                            int label;
                            final /* synthetic */ MutableState<Set<String>> $selectedAlbumRecordIds$delegate;
                            final /* synthetic */ MutableState<Boolean> $albumSelectionModeActive$delegate;
                            final /* synthetic */ MutableState<Boolean> $showRemoveFromAlbumConfirm$delegate;
                            final /* synthetic */ MutableState<Boolean> $showDeleteSelectedConfirm$delegate;
                            {
                                this.$selectedAlbumRecordIds$delegate = $selectedAlbumRecordIds$delegate;
                                this.$albumSelectionModeActive$delegate = $albumSelectionModeActive$delegate;
                                this.$showRemoveFromAlbumConfirm$delegate = $showRemoveFromAlbumConfirm$delegate;
                                this.$showDeleteSelectedConfirm$delegate = $showDeleteSelectedConfirm$delegate;
                                super(2, $completion);
                            }

                            public final Object invokeSuspend(Object $result) {
                                IntrinsicsKt.getCOROUTINE_SUSPENDED();
                                switch (this.label) {
                                    case 0: {
                                        ResultKt.throwOnFailure((Object)$result);
                                        GroupActivity.access$GroupContent$lambda$65(this.$selectedAlbumRecordIds$delegate, SetsKt.emptySet());
                                        GroupActivity.access$GroupContent$lambda$68(this.$albumSelectionModeActive$delegate, false);
                                        GroupActivity.access$GroupContent$lambda$71(this.$showRemoveFromAlbumConfirm$delegate, false);
                                        GroupActivity.access$GroupContent$lambda$74(this.$showDeleteSelectedConfirm$delegate, false);
                                        return Unit.INSTANCE;
                                    }
                                }
                                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                            }

                            public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
                                return (Continuation)new /* invalid duplicate definition of identical inner class */;
                            }

                            public final Object invoke(CoroutineScope p1, Continuation<? super Unit> p2) {
                                return (this.create(p1, p2)).invokeSuspend(Unit.INSTANCE);
                            }
                        };
                        v53 = var97_255;
                        value\161 /* !! */  = var98_261 /* !! */ ;
                        $this$cache\160.updateRememberedValue((Object)value\161 /* !! */ );
                        v54 = value\161 /* !! */ ;
                    } else {
                        v54 = it\160;
                    }
                    var80_309 = (Function2)v54;
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
                    EffectsKt.LaunchedEffect((Object)v53, (Function2)var80_309, (Composer)$composer, (int)0);
                    v55 = openedAlbum;
                    v56 = v55 != null ? v55.getAlbumId() : null;
                    v57 = openedAlbum;
                    v58 = v57 != null ? v57.getOrganizeStatus() : null;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)541179988, (String)"CC(remember):GroupActivity.kt#9igjgp");
                    $this$cache\160 = $composer;
                    invalid\163 = $composer.changedInstance((Object)openedAlbum) | $composer.changedInstance((Object)albumStore);
                    $i$f$cache\163\455 = false;
                    it\163 = $this$cache\163.rememberedValue();
                    $i$a$-let-ComposerKt$cache$1\164\2799\163 = false;
                    if (invalid\163 || it\163 == Composer.Companion.getEmpty()) {
                        var98_261 /* !! */  = v58;
                        var97_255 = v56;
                        $i$a$-cache-GroupActivity$GroupContent$4\165\2801\0 = false;
                        var99_373 /* !! */  = (Function2)new Function2<CoroutineScope, Continuation<? super Unit>, Object>(openedAlbum, albumStore, (MutableState<List<GroupAlbumStore.GroupAlbum>>)albumList$delegate, null){
                            int label;
                            final /* synthetic */ GroupAlbumStore.GroupAlbum $openedAlbum;
                            final /* synthetic */ GroupAlbumStore $albumStore;
                            final /* synthetic */ MutableState<List<GroupAlbumStore.GroupAlbum>> $albumList$delegate;
                            {
                                this.$openedAlbum = $openedAlbum;
                                this.$albumStore = $albumStore;
                                this.$albumList$delegate = $albumList$delegate;
                                super(2, $completion);
                            }

                            public final Object invokeSuspend(Object $result) {
                                IntrinsicsKt.getCOROUTINE_SUSPENDED();
                                switch (this.label) {
                                    case 0: {
                                        ResultKt.throwOnFailure((Object)$result);
                                        GroupAlbumStore.GroupAlbum groupAlbum = this.$openedAlbum;
                                        if (groupAlbum == null) {
                                            return Unit.INSTANCE;
                                        }
                                        GroupAlbumStore.GroupAlbum currentAlbum = groupAlbum;
                                        if (Intrinsics.areEqual((Object)currentAlbum.getOrganizeStatus(), (Object)"completed") && this.$albumStore.updateOrganizeStatus(currentAlbum.getAlbumId(), "idle")) {
                                            GroupActivity.access$GroupContent$lambda$35(this.$albumList$delegate, this.$albumStore.loadAlbums());
                                        }
                                        return Unit.INSTANCE;
                                    }
                                }
                                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                            }

                            public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
                                return (Continuation)new /* invalid duplicate definition of identical inner class */;
                            }

                            public final Object invoke(CoroutineScope p1, Continuation<? super Unit> p2) {
                                return (this.create(p1, p2)).invokeSuspend(Unit.INSTANCE);
                            }
                        };
                        v56 = var97_255;
                        v58 = var98_261 /* !! */ ;
                        value\164 = var99_373 /* !! */ ;
                        $this$cache\163.updateRememberedValue((Object)value\164);
                        v59 = value\164;
                    } else {
                        v59 = it\163;
                    }
                    var80_309 = (Function2)v59;
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
                    EffectsKt.LaunchedEffect((Object)v56, (Object)v58, (Function2)var80_309, (Composer)$composer, (int)0);
                    v60 = openedRecords /* !! */ ;
                    v61 /* !! */  = GroupActivity.GroupContent$lambda$64((MutableState<Set<String>>)selectedAlbumRecordIds$delegate);
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)541194140, (String)"CC(remember):GroupActivity.kt#9igjgp");
                    $this$cache\163 = $composer;
                    invalid\166 = $composer.changedInstance((Object)openedRecords /* !! */ );
                    $i$f$cache\166\463 = false;
                    it\166 = $this$cache\166.rememberedValue();
                    $i$a$-let-ComposerKt$cache$1\167\2805\166 = false;
                    if (invalid\166 || it\166 == Composer.Companion.getEmpty()) {
                        var98_261 /* !! */  = v61 /* !! */ ;
                        var97_255 = v60;
                        $i$a$-cache-GroupActivity$GroupContent$5\168\2807\0 = false;
                        var99_373 /* !! */  = (Function2)new Function2<CoroutineScope, Continuation<? super Unit>, Object>((List<? extends MemoryRecord>)openedRecords /* !! */ , (MutableState<Set<String>>)selectedAlbumRecordIds$delegate, (MutableState<Boolean>)albumSelectionModeActive$delegate, (MutableState<Boolean>)showRemoveFromAlbumConfirm$delegate, (MutableState<Boolean>)showDeleteSelectedConfirm$delegate, null){
                            int label;
                            final /* synthetic */ List<MemoryRecord> $openedRecords;
                            final /* synthetic */ MutableState<Set<String>> $selectedAlbumRecordIds$delegate;
                            final /* synthetic */ MutableState<Boolean> $albumSelectionModeActive$delegate;
                            final /* synthetic */ MutableState<Boolean> $showRemoveFromAlbumConfirm$delegate;
                            final /* synthetic */ MutableState<Boolean> $showDeleteSelectedConfirm$delegate;
                            {
                                this.$openedRecords = $openedRecords;
                                this.$selectedAlbumRecordIds$delegate = $selectedAlbumRecordIds$delegate;
                                this.$albumSelectionModeActive$delegate = $albumSelectionModeActive$delegate;
                                this.$showRemoveFromAlbumConfirm$delegate = $showRemoveFromAlbumConfirm$delegate;
                                this.$showDeleteSelectedConfirm$delegate = $showDeleteSelectedConfirm$delegate;
                                super(2, $completion);
                            }

                            /*
                             * WARNING - void declaration
                             */
                            public final Object invokeSuspend(Object $result) {
                                IntrinsicsKt.getCOROUTINE_SUSPENDED();
                                switch (this.label) {
                                    case 0: {
                                        void destination\4;
                                        void $this$filterTo\4;
                                        Collection collection;
                                        ResultKt.throwOnFailure((Object)$result);
                                        Iterable iterable = this.$openedRecords;
                                        boolean $i$f$map\1\4652 = false;
                                        Iterable iterable2 = iterable;
                                        Collection collection2 = new ArrayList<E>(CollectionsKt.collectionSizeOrDefault((Iterable)iterable, (int)10));
                                        boolean bl = false;
                                        for (T t : collection) {
                                            void it\3;
                                            MemoryRecord memoryRecord = (MemoryRecord)t;
                                            Collection collection3 = collection2;
                                            boolean bl2 = false;
                                            collection3.add(it\3.getRecordId());
                                        }
                                        Set validIds = CollectionsKt.toSet((Iterable)((List)collection2));
                                        Iterable $i$f$map\1\4652 = GroupActivity.access$GroupContent$lambda$64(this.$selectedAlbumRecordIds$delegate);
                                        collection = new LinkedHashSet<E>();
                                        boolean bl3 = false;
                                        for (T t : $this$filterTo\4) {
                                            String string2 = (String)t;
                                            boolean bl4 = false;
                                            if (!validIds.contains(string2)) continue;
                                            destination\4.add(t);
                                        }
                                        Set sanitized = CollectionsKt.toSet((Iterable)((Iterable)destination\4));
                                        if (!Intrinsics.areEqual((Object)sanitized, (Object)GroupActivity.access$GroupContent$lambda$64(this.$selectedAlbumRecordIds$delegate))) {
                                            GroupActivity.access$GroupContent$lambda$65(this.$selectedAlbumRecordIds$delegate, sanitized);
                                        }
                                        if (sanitized.isEmpty() && !((Collection)this.$openedRecords).isEmpty()) {
                                            GroupActivity.access$GroupContent$lambda$68(this.$albumSelectionModeActive$delegate, false);
                                            GroupActivity.access$GroupContent$lambda$71(this.$showRemoveFromAlbumConfirm$delegate, false);
                                            GroupActivity.access$GroupContent$lambda$74(this.$showDeleteSelectedConfirm$delegate, false);
                                        }
                                        return Unit.INSTANCE;
                                    }
                                }
                                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                            }

                            public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
                                return (Continuation)new /* invalid duplicate definition of identical inner class */;
                            }

                            public final Object invoke(CoroutineScope p1, Continuation<? super Unit> p2) {
                                return (this.create(p1, p2)).invokeSuspend(Unit.INSTANCE);
                            }
                        };
                        v60 = var97_255;
                        v61 /* !! */  = var98_261 /* !! */ ;
                        value\167 = var99_373 /* !! */ ;
                        $this$cache\166.updateRememberedValue((Object)value\167);
                        v62 = value\167;
                    } else {
                        v62 = it\166;
                    }
                    var80_309 = (Function2)v62;
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
                    EffectsKt.LaunchedEffect((Object)v60, v61 /* !! */ , (Function2)var80_309, (Composer)$composer, (int)0);
                    v63 = openedAlbum != null && showAddSheet == false && GroupActivity.GroupContent$lambda$40((MutableState<Boolean>)showCreateAlbumDialog$delegate) == false && GroupActivity.GroupContent$lambda$43((MutableState<Boolean>)showAddExistingSheet$delegate) == false && GroupActivity.GroupContent$lambda$76((MutableState<Boolean>)showEditAlbumDialog$delegate) == false && GroupActivity.GroupContent$lambda$55((MutableState<Boolean>)detailMoreExpanded$delegate) == false && GroupActivity.GroupContent$lambda$67((MutableState<Boolean>)albumSelectionModeActive$delegate) == false;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)541221193, (String)"CC(remember):GroupActivity.kt#9igjgp");
                    $this$cache\166 = $composer;
                    invalid\169 = ($dirty1 & 0x1C00000) == 0x800000 | ($dirty1 & 0x70000000) == 0x20000000 | $composer.changedInstance((Object)this);
                    $i$f$cache\169\483 = false;
                    it\169 = $this$cache\169.rememberedValue();
                    $i$a$-let-ComposerKt$cache$1\170\2811\169 = false;
                    if (invalid\169 || it\169 == Composer.Companion.getEmpty()) {
                        var97_256 = v63;
                        $i$a$-cache-GroupActivity$GroupContent$6\171\2813\0 = false;
                        var98_261 /* !! */  = (Function0)LambdaMetafactory.metafactory(null, null, null, ()Ljava/lang/Object;, GroupContent$lambda$133$lambda$132(boolean kotlin.jvm.functions.Function0 com.han.nomemo.GroupActivity androidx.compose.runtime.MutableState ), ()Lkotlin/Unit;)((boolean)openedAsStandaloneDetail, onCloseAlbumDetail, (GroupActivity)this, (MutableState)openedAlbumId$delegate);
                        v63 = var97_256;
                        value\170 /* !! */  = var98_261 /* !! */ ;
                        $this$cache\169.updateRememberedValue((Object)value\170 /* !! */ );
                        v64 = value\170 /* !! */ ;
                    } else {
                        v64 = it\169;
                    }
                    var80_309 = (Function0)v64;
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
                    BackHandlerKt.BackHandler((boolean)v63, (Function0)var80_309, (Composer)$composer, (int)0, (int)0);
                    v65 = GroupActivity.GroupContent$lambda$67((MutableState<Boolean>)albumSelectionModeActive$delegate);
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)541229507, (String)"CC(remember):GroupActivity.kt#9igjgp");
                    $this$cache\169 = $composer;
                    invalid\172 = false;
                    $i$f$cache\172\491 = false;
                    it\172 = $this$cache\172.rememberedValue();
                    $i$a$-let-ComposerKt$cache$1\173\2817\172 = false;
                    if (it\172 == Composer.Companion.getEmpty()) {
                        var97_257 = v65;
                        $i$a$-cache-GroupActivity$GroupContent$7\174\2819\0 = false;
                        var98_261 /* !! */  = (Function0)LambdaMetafactory.metafactory(null, null, null, ()Ljava/lang/Object;, GroupContent$lambda$135$lambda$134(androidx.compose.runtime.MutableState androidx.compose.runtime.MutableState androidx.compose.runtime.MutableState androidx.compose.runtime.MutableState ), ()Lkotlin/Unit;)((MutableState)albumSelectionModeActive$delegate, (MutableState)selectedAlbumRecordIds$delegate, (MutableState)showRemoveFromAlbumConfirm$delegate, (MutableState)showDeleteSelectedConfirm$delegate);
                        v65 = var97_257;
                        value\173 /* !! */  = var98_261 /* !! */ ;
                        $this$cache\172.updateRememberedValue((Object)value\173 /* !! */ );
                        v66 = value\173 /* !! */ ;
                    } else {
                        v66 = it\172;
                    }
                    var80_309 = (Function0)v66;
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
                    BackHandlerKt.BackHandler((boolean)v65, (Function0)var80_309, (Composer)$composer, (int)48, (int)0);
                    v67 = openedAsStandaloneDetail;
                    v68 = GroupActivity.GroupContent$lambda$37((MutableState<String>)openedAlbumId$delegate);
                    v69 = openedAlbum;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)541238264, (String)"CC(remember):GroupActivity.kt#9igjgp");
                    $this$cache\172 = $composer;
                    invalid\175 = ($dirty1 & 0x1C00000) == 0x800000 | $composer.changedInstance((Object)openedAlbum) | ($dirty1 & 0x70000000) == 0x20000000;
                    $i$f$cache\175\497 = false;
                    it\175 = $this$cache\175.rememberedValue();
                    $i$a$-let-ComposerKt$cache$1\176\2823\175 = false;
                    if (invalid\175 || it\175 == Composer.Companion.getEmpty()) {
                        var99_373 /* !! */  = v69;
                        var98_261 /* !! */  = v68;
                        var97_258 = v67;
                        $i$a$-cache-GroupActivity$GroupContent$8\177\2825\0 = false;
                        var100_374 = (Function2)new Function2<CoroutineScope, Continuation<? super Unit>, Object>(openedAsStandaloneDetail, openedAlbum, onCloseAlbumDetail, (MutableState<String>)openedAlbumId$delegate, null){
                            int label;
                            final /* synthetic */ boolean $openedAsStandaloneDetail;
                            final /* synthetic */ GroupAlbumStore.GroupAlbum $openedAlbum;
                            final /* synthetic */ Function0<Unit> $onCloseAlbumDetail;
                            final /* synthetic */ MutableState<String> $openedAlbumId$delegate;
                            {
                                this.$openedAsStandaloneDetail = $openedAsStandaloneDetail;
                                this.$openedAlbum = $openedAlbum;
                                this.$onCloseAlbumDetail = $onCloseAlbumDetail;
                                this.$openedAlbumId$delegate = $openedAlbumId$delegate;
                                super(2, $completion);
                            }

                            public final Object invokeSuspend(Object $result) {
                                IntrinsicsKt.getCOROUTINE_SUSPENDED();
                                switch (this.label) {
                                    case 0: {
                                        ResultKt.throwOnFailure((Object)$result);
                                        if (this.$openedAsStandaloneDetail && GroupActivity.access$GroupContent$lambda$37(this.$openedAlbumId$delegate) != null && this.$openedAlbum == null) {
                                            this.$onCloseAlbumDetail.invoke();
                                        }
                                        return Unit.INSTANCE;
                                    }
                                }
                                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                            }

                            public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
                                return (Continuation)new /* invalid duplicate definition of identical inner class */;
                            }

                            public final Object invoke(CoroutineScope p1, Continuation<? super Unit> p2) {
                                return (this.create(p1, p2)).invokeSuspend(Unit.INSTANCE);
                            }
                        };
                        v67 = var97_258;
                        v68 = var98_261 /* !! */ ;
                        v69 = var99_373 /* !! */ ;
                        value\176 = var100_374;
                        $this$cache\175.updateRememberedValue((Object)value\176);
                        v70 = value\176;
                    } else {
                        v70 = it\175;
                    }
                    var80_309 = (Function2)v70;
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
                    EffectsKt.LaunchedEffect((Object)v67, (Object)v68, (Object)v69, (Function2)var80_309, (Composer)$composer, (int)(14 & $dirty1 >> 21 | GroupAlbumStore.GroupAlbum.$stable << 6));
                    v71 = GroupActivity.GroupContent$lambda$55((MutableState<Boolean>)detailMoreExpanded$delegate);
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)541244720, (String)"CC(remember):GroupActivity.kt#9igjgp");
                    $this$cache\175 = $composer;
                    invalid\178 = false;
                    $i$f$cache\178\502 = false;
                    it\178 = $this$cache\178.rememberedValue();
                    $i$a$-let-ComposerKt$cache$1\179\2829\178 = false;
                    if (it\178 == Composer.Companion.getEmpty()) {
                        var97_259 = v71;
                        $i$a$-cache-GroupActivity$GroupContent$9\180\2831\0 = false;
                        var98_261 /* !! */  = (Function0)LambdaMetafactory.metafactory(null, null, null, ()Ljava/lang/Object;, GroupContent$lambda$138$lambda$137(androidx.compose.runtime.MutableState ), ()Lkotlin/Unit;)((MutableState)detailMoreExpanded$delegate);
                        v71 = var97_259;
                        value\179 /* !! */  = var98_261 /* !! */ ;
                        $this$cache\178.updateRememberedValue((Object)value\179 /* !! */ );
                        v72 = value\179 /* !! */ ;
                    } else {
                        v72 = it\178;
                    }
                    var80_309 = (Function0)v72;
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
                    BackHandlerKt.BackHandler((boolean)v71, (Function0)var80_309, (Composer)$composer, (int)48, (int)0);
                    v73 = openedAlbum == null && GroupActivity.GroupContent$lambda$52((MutableState<Boolean>)groupListMoreExpanded$delegate) != false && showAddSheet == false && GroupActivity.GroupContent$lambda$40((MutableState<Boolean>)showCreateAlbumDialog$delegate) == false && GroupActivity.GroupContent$lambda$43((MutableState<Boolean>)showAddExistingSheet$delegate) == false && GroupActivity.GroupContent$lambda$76((MutableState<Boolean>)showEditAlbumDialog$delegate) == false;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)541254995, (String)"CC(remember):GroupActivity.kt#9igjgp");
                    $this$cache\178 = $composer;
                    invalid\181 = false;
                    $i$f$cache\181\512 = false;
                    it\181 = $this$cache\181.rememberedValue();
                    $i$a$-let-ComposerKt$cache$1\182\2835\181 = false;
                    if (it\181 == Composer.Companion.getEmpty()) {
                        var97_260 = v73;
                        $i$a$-cache-GroupActivity$GroupContent$10\183\2837\0 = false;
                        var98_261 /* !! */  = (Function0)LambdaMetafactory.metafactory(null, null, null, ()Ljava/lang/Object;, GroupContent$lambda$140$lambda$139(androidx.compose.runtime.MutableState ), ()Lkotlin/Unit;)((MutableState)groupListMoreExpanded$delegate);
                        v73 = var97_260;
                        value\182 /* !! */  = var98_261 /* !! */ ;
                        $this$cache\181.updateRememberedValue((Object)value\182 /* !! */ );
                        v74 = value\182 /* !! */ ;
                    } else {
                        v74 = it\181;
                    }
                    var80_309 = (Function0)v74;
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
                    BackHandlerKt.BackHandler((boolean)v73, (Function0)var80_309, (Composer)$composer, (int)48, (int)0);
                    ComposeUiKt.NoMemoBackground(null, (Function4<? super BoxScope, ? super NoMemoPalette, ? super Composer, ? super Integer, Unit>)((Function4)ComposableLambdaKt.rememberComposableLambda((int)862704436, (boolean)true, (Object)(Function4)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;, GroupContent$lambda$259(com.han.nomemo.NoMemoAdaptiveSpec com.han.nomemo.GroupAlbumStore$GroupAlbum kotlin.jvm.functions.Function0 kotlin.jvm.functions.Function0 kotlin.jvm.functions.Function0 com.han.nomemo.NoMemoDockTab java.util.List boolean com.han.nomemo.GroupActivity android.content.Context kotlin.jvm.functions.Function0 java.util.List com.han.nomemo.SettingsStore com.han.nomemo.GroupAlbumStore java.util.List boolean kotlin.jvm.functions.Function0 kotlin.jvm.functions.Function1 boolean kotlin.jvm.functions.Function0 float float androidx.compose.foundation.lazy.LazyListState float java.util.List java.util.Map kotlin.jvm.functions.Function1 int androidx.compose.foundation.lazy.LazyListState com.han.nomemo.NoMemoPalette kotlin.jvm.functions.Function1 androidx.compose.runtime.MutableState kotlin.jvm.functions.Function0 float androidx.compose.runtime.MutableState androidx.compose.runtime.MutableState androidx.compose.runtime.State androidx.compose.runtime.MutableState androidx.compose.runtime.State androidx.compose.runtime.MutableState androidx.compose.runtime.MutableState androidx.compose.runtime.MutableState androidx.compose.runtime.MutableState androidx.compose.runtime.MutableState androidx.compose.runtime.MutableState androidx.compose.runtime.MutableState androidx.compose.runtime.MutableState androidx.compose.runtime.MutableState androidx.compose.runtime.MutableState androidx.compose.runtime.MutableState androidx.compose.runtime.MutableState androidx.compose.runtime.MutableState androidx.compose.runtime.MutableState androidx.compose.runtime.MutableState androidx.compose.runtime.MutableState androidx.compose.runtime.MutableState androidx.compose.foundation.layout.BoxScope com.han.nomemo.NoMemoPalette androidx.compose.runtime.Composer int ), (Landroidx/compose/foundation/layout/BoxScope;Lcom/han/nomemo/NoMemoPalette;Landroidx/compose/runtime/Composer;Ljava/lang/Integer;)Lkotlin/Unit;)((NoMemoAdaptiveSpec)albumAdaptive, (GroupAlbumStore.GroupAlbum)openedAlbum, onOpenMemory, onOpenReminder, onAddClick, (NoMemoDockTab)startupDockPulseTab, (List)selectedAlbumRecords, (boolean)allOpenedRecordsSelected, (GroupActivity)this, (Context)albumContext, onOpenSettings, (List)openedRecords /* !! */ , (SettingsStore)settingsStore, (GroupAlbumStore)albumStore, (List)filteredExistingRecords, (boolean)openedAsStandaloneDetail, onCloseAlbumDetail, onDeleteRecords, (boolean)showAddSheet, onDismissAddSheet, (float)groupExpandedTitleAlpha, (float)groupExpandedTitleTranslateY, (LazyListState)groupListState, (float)groupListSpacing, (List)albumRows, (Map)albumPreviewRecordsMap, onOpenAlbumDetail, (int)albumColumns, (LazyListState)albumDetailListState, (NoMemoPalette)albumPalette, onOpenDetail, (MutableState)closingStandaloneDetail$delegate, onOpenSearch, (float)groupCollapsedTitleAlpha, (MutableState)groupListMoreExpanded$delegate, (MutableState)groupListMoreAnchorBounds$delegate, (State)groupExpandedTitleHeight$delegate, (MutableState)albumList$delegate, (State)groupListTopPadding$delegate, (MutableState)albumSelectionModeActive$delegate, (MutableState)selectedAlbumRecordIds$delegate, (MutableState)showRemoveFromAlbumConfirm$delegate, (MutableState)showDeleteSelectedConfirm$delegate, (MutableState)openedAlbumId$delegate, (MutableState)detailMoreExpanded$delegate, (MutableState)detailMoreAnchorBounds$delegate, (MutableState)selectedExistingRecordIds$delegate, (MutableState)addExistingSearchQuery$delegate, (MutableState)showAddExistingSheet$delegate, (MutableState)showCreateAlbumDialog$delegate, (MutableState)editingAlbumId$delegate, (MutableState)albumNameInput$delegate, (MutableState)albumDescriptionInput$delegate, (MutableState)showEditAlbumDialog$delegate, (MutableState)showDeleteAlbumConfirm$delegate, (MutableState)albumAutoClassifyEnabledInput$delegate), (Composer)$composer, (int)54)), $composer, 48, 1);
                    if (ComposerKt.isTraceInProgress()) {
                        ComposerKt.traceEventEnd();
                    }
                    break block135;
                }
                $composer.skipToGroupEnd();
            }
            v75 = var28_37 = $composer.endRestartGroup();
            if (v75 == null) break block136;
            v75.updateScope((Function2)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;, GroupContent$lambda$260(com.han.nomemo.GroupActivity java.util.List boolean java.lang.String kotlin.jvm.functions.Function1 kotlin.jvm.functions.Function1 kotlin.jvm.functions.Function1 kotlin.jvm.functions.Function1 kotlin.jvm.functions.Function0 kotlin.jvm.functions.Function0 kotlin.jvm.functions.Function0 kotlin.jvm.functions.Function0 boolean kotlin.jvm.functions.Function0 kotlin.jvm.functions.Function0 com.han.nomemo.NoMemoDockTab int java.lang.String boolean kotlin.jvm.functions.Function1 kotlin.jvm.functions.Function0 int int int androidx.compose.runtime.Composer int ), (Landroidx/compose/runtime/Composer;Ljava/lang/Integer;)Lkotlin/Unit;)((GroupActivity)this, allRecords, (boolean)hasLoadedRecords, (String)selectedCategoryCode, onSelectCategory, onDeleteRecord, onDeleteRecords, onOpenDetail, onOpenMemory, onOpenReminder, onOpenSearch, onOpenSettings, (boolean)showAddSheet, onAddClick, onDismissAddSheet, (NoMemoDockTab)startupDockPulseTab, (int)albumRefreshTick, (String)initialOpenedAlbumId, (boolean)openedAsStandaloneDetail, onOpenAlbumDetail, onCloseAlbumDetail, (int)$changed, (int)$changed1, (int)$changed2));
        }
    }

    /*
     * WARNING - void declaration
     */
    @Composable
    @ComposableTarget(applier="androidx.compose.ui.UiComposable")
    private final void GroupAlbumGridCard(GroupAlbumStore.GroupAlbum album, boolean compact, int memoryCount, List<? extends MemoryRecord> previewRecords, Modifier modifier, Function0<Unit> onClick, Composer $composer, int $changed, int n) {
        block18: {
            $composer = $composer.startRestartGroup(-785045907);
            ComposerKt.sourceInformation((Composer)$composer, (String)"C(GroupAlbumGridCard)N(album,compact,memoryCount,previewRecords,modifier,onClick)1166@59866L23,1167@59911L21,1168@59955L131,1173@60226L877,1173@60172L931:GroupActivity.kt#83vr7l");
            int $dirty = $changed;
            if (($changed & 6) == 0) {
                $dirty |= (($changed & 8) == 0 ? $composer.changed((Object)album) : $composer.changedInstance((Object)album)) ? 4 : 2;
            }
            if (($changed & 0x30) == 0) {
                $dirty |= $composer.changed(compact) ? 32 : 16;
            }
            if (($changed & 0x180) == 0) {
                $dirty |= $composer.changed(memoryCount) ? 256 : 128;
            }
            if (($changed & 0xC00) == 0) {
                $dirty |= $composer.changedInstance(previewRecords) ? 2048 : 1024;
            }
            if ((n & 0x10) != 0) {
                $dirty |= 0x6000;
            } else if (($changed & 0x6000) == 0) {
                $dirty |= $composer.changed((Object)modifier) ? 16384 : 8192;
            }
            if (($changed & 0x30000) == 0) {
                $dirty |= $composer.changedInstance(onClick) ? 131072 : 65536;
            }
            if (($changed & 0x180000) == 0) {
                $dirty |= $composer.changedInstance((Object)this) ? 0x100000 : 524288;
            }
            if ($composer.shouldExecute(($dirty & 0x92493) != 599186, $dirty & 1)) {
                float f;
                Object object;
                void $this$cache\1;
                if ((n & 0x10) != 0) {
                    modifier = (Modifier)Modifier.Companion;
                }
                if (ComposerKt.isTraceInProgress()) {
                    ComposerKt.traceEventStart((int)-785045907, (int)$dirty, (int)-1, (String)"com.han.nomemo.GroupActivity.GroupAlbumGridCard (GroupActivity.kt:1165)");
                }
                NoMemoPalette palette = ComposeUiKt.rememberNoMemoPalette($composer, 0);
                boolean isDark = DarkThemeKt.isSystemInDarkTheme((Composer)$composer, (int)0);
                long l = album.getCreatedAt();
                ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)1485139536, (String)"CC(remember):GroupActivity.kt#9igjgp");
                Composer composer = $composer;
                boolean bl = $composer.changed(l);
                boolean bl2 = false;
                Object object2 = $this$cache\1.rememberedValue();
                boolean bl3 = false;
                if (bl || object2 == Composer.Companion.getEmpty()) {
                    boolean bl4 = false;
                    String string2 = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).format(new Date(album.getCreatedAt()));
                    $this$cache\1.updateRememberedValue((Object)string2);
                    object = string2;
                } else {
                    object = object2;
                }
                String string3 = (String)object;
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
                String dayText = string3;
                if (compact) {
                    int n2 = 24;
                    boolean bl5 = false;
                    f = Dp.constructor-impl((float)n2);
                } else {
                    int n3 = 26;
                    boolean bl6 = false;
                    f = Dp.constructor-impl((float)n3);
                }
                ContinuousRoundedRectangle cardShape = ComposeUiKt.noMemoG2RoundedShape-0680j_4(f);
                ComposeUiKt.PressScaleBox(onClick, modifier, 0.0f, null, (Function3<? super BoxScope, ? super Composer, ? super Integer, Unit>)((Function3)ComposableLambdaKt.rememberComposableLambda((int)1200204427, (boolean)true, (arg_0, arg_1, arg_2) -> GroupActivity.GroupAlbumGridCard$lambda$263(cardShape, isDark, this, album, previewRecords, memoryCount, dayText, compact, arg_0, arg_1, arg_2), (Composer)$composer, (int)54)), $composer, 0x6000 | 0xE & $dirty >> 15 | 0x70 & $dirty >> 9, 12);
                if (ComposerKt.isTraceInProgress()) {
                    ComposerKt.traceEventEnd();
                }
            } else {
                $composer.skipToGroupEnd();
            }
            ScopeUpdateScope scopeUpdateScope = $composer.endRestartGroup();
            if (scopeUpdateScope == null) break block18;
            scopeUpdateScope.updateScope((arg_0, arg_1) -> GroupActivity.GroupAlbumGridCard$lambda$264(this, album, compact, memoryCount, previewRecords, modifier, onClick, $changed, n, arg_0, arg_1));
        }
    }

    /*
     * WARNING - void declaration
     */
    @Composable
    @ComposableTarget(applier="androidx.compose.ui.UiComposable")
    private final void GroupAlbumCoverCollage(String albumId, String albumName, List<? extends MemoryRecord> previewRecords, int memoryCount, String dayText, boolean compact, Modifier modifier, Composer $composer, int $changed, int n) {
        block65: {
            ScopeUpdateScope scopeUpdateScope;
            $composer = $composer.startRestartGroup(1286299262);
            ComposerKt.sourceInformation((Composer)$composer, (String)"C(GroupAlbumCoverCollage)N(albumId,albumName,previewRecords,memoryCount,dayText,compact,modifier)1210@61416L21,1211@61460L23,1218@61746L8307:GroupActivity.kt#83vr7l");
            int $dirty = $changed;
            if (($changed & 6) == 0) {
                $dirty |= $composer.changed((Object)albumId) ? 4 : 2;
            }
            if (($changed & 0x30) == 0) {
                $dirty |= $composer.changed((Object)albumName) ? 32 : 16;
            }
            if (($changed & 0x180) == 0) {
                $dirty |= $composer.changedInstance(previewRecords) ? 256 : 128;
            }
            if (($changed & 0xC00) == 0) {
                $dirty |= $composer.changed(memoryCount) ? 2048 : 1024;
            }
            if (($changed & 0x6000) == 0) {
                $dirty |= $composer.changed((Object)dayText) ? 16384 : 8192;
            }
            if (($changed & 0x30000) == 0) {
                $dirty |= $composer.changed(compact) ? 131072 : 65536;
            }
            if ((n & 0x40) != 0) {
                $dirty |= 0x180000;
            } else if (($changed & 0x180000) == 0) {
                $dirty |= $composer.changed((Object)modifier) ? 0x100000 : 524288;
            }
            if (($changed & 0xC00000) == 0) {
                $dirty |= $composer.changedInstance((Object)this) ? 0x800000 : 0x400000;
            }
            if ($composer.shouldExecute(($dirty & 0x492493) != 0x492492, $dirty & 1)) {
                void $composer\94;
                void $changed\89;
                void $changed\88;
                void modifier\88;
                void modifier\87;
                void $changed\87;
                void verticalAlignment\87;
                void $composer\87;
                void $composer\85;
                void $changed\80;
                void $changed\79;
                void modifier\79;
                void modifier\78;
                void $changed\78;
                void $composer\78;
                void $composer\74;
                int n2;
                Function0 function0;
                int n3;
                void modifier\68;
                boolean bl;
                void modifier\67;
                void $composer\67;
                void $composer\62;
                int n4;
                Function0 function02;
                int n5;
                void modifier\56;
                Alignment.Vertical vertical;
                int n6;
                void $composer\54;
                int n7;
                Function0 function03;
                int n8;
                void modifier\48;
                Object[] objectArray;
                boolean bl2;
                void $composer\47;
                int n9;
                Painter painter;
                void $composer\44;
                int n10;
                Function0 function04;
                int n11;
                void modifier\38;
                int n12;
                Modifier modifier2;
                Arrangement.Horizontal horizontal;
                void $composer\37;
                List list;
                Object[] objectArray22;
                float f;
                void $composer\17;
                void $changed\12;
                void $changed\11;
                void modifier\11;
                void $changed\10;
                void modifier\10;
                void $composer\10;
                float f2;
                float f3;
                float tileCorner;
                float f4;
                float coverCorner;
                float f5;
                if ((n & 0x40) != 0) {
                    modifier = (Modifier)Modifier.Companion;
                }
                if (ComposerKt.isTraceInProgress()) {
                    ComposerKt.traceEventStart((int)1286299262, (int)$dirty, (int)-1, (String)"com.han.nomemo.GroupActivity.GroupAlbumCoverCollage (GroupActivity.kt:1209)");
                }
                boolean isDark = DarkThemeKt.isSystemInDarkTheme((Composer)$composer, (int)0);
                NoMemoPalette palette = ComposeUiKt.rememberNoMemoPalette($composer, 0);
                if (compact) {
                    int n13 = 18;
                    boolean bl3 = false;
                    f5 = Dp.constructor-impl((float)n13);
                } else {
                    int n14 = 20;
                    boolean bl4 = false;
                    f5 = coverCorner = Dp.constructor-impl((float)n14);
                }
                if (compact) {
                    int n15 = 15;
                    boolean bl5 = false;
                    f4 = Dp.constructor-impl((float)n15);
                } else {
                    int n16 = 16;
                    boolean bl6 = false;
                    f4 = tileCorner = Dp.constructor-impl((float)n16);
                }
                if (compact) {
                    int n17 = 174;
                    boolean bl7 = false;
                    f3 = Dp.constructor-impl((float)n17);
                } else {
                    int n18 = 194;
                    boolean bl8 = false;
                    f3 = Dp.constructor-impl((float)n18);
                }
                float coverHeight = f3;
                int n19 = 6;
                boolean bl9 = false;
                float tileGap = Dp.constructor-impl((float)n19);
                if (compact) {
                    int n20 = 8;
                    boolean bl10 = false;
                    f2 = Dp.constructor-impl((float)n20);
                } else {
                    int n21 = 9;
                    boolean bl11 = false;
                    f2 = Dp.constructor-impl((float)n21);
                }
                float collagePadding = f2;
                Modifier n21 = BackgroundKt.background$default((Modifier)ClipKt.clip((Modifier)SizeKt.height-3ABfNKs((Modifier)SizeKt.fillMaxWidth$default((Modifier)modifier, (float)0.0f, (int)1, null), (float)coverHeight), (Shape)((Shape)ComposeUiKt.noMemoG2RoundedShape-0680j_4(coverCorner))), (Brush)this.groupAlbumCoverBrush(albumId, isDark), null, (float)0.0f, (int)6, null);
                Composer composer = $composer;
                boolean bl12 = false;
                boolean bl13 = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\10, (int)1042775818, (String)"CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo");
                Alignment alignment = Alignment.Companion.getTopStart();
                boolean bl14 = false;
                MeasurePolicy measurePolicy = BoxKt.maybeCachedBoxMeasurePolicy((Alignment)alignment, (boolean)bl14);
                void var26_33 = modifier\10;
                int n22 = 0x70 & $changed\10 << 3;
                boolean bl15 = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\10, (int)-1159599143, (String)"CC(Layout)P(!1,2)80@3267L27,83@3433L360:Layout.kt#80mrfh");
                int n23 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode((Composer)$composer\10, (int)0));
                CompositionLocalMap compositionLocalMap = $composer\10.getCurrentCompositionLocalMap();
                Modifier modifier3 = ComposedModifierKt.materializeModifier((Composer)$composer\10, (Modifier)modifier\11);
                Function0 function05 = ComposeUiNode.Companion.getConstructor();
                int n24 = 6 | 0x380 & $changed\11 << 6;
                boolean bl16 = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\10, (int)-553112988, (String)"CC(ReusableComposeNode)N(factory,update,content)399@15590L9:Composables.kt#9igjgp");
                if (!($composer\10.getApplier() instanceof Applier)) {
                    ComposablesKt.invalidApplier();
                }
                $composer\10.startReusableNode();
                if ($composer\10.getInserting()) {
                    void factory\12;
                    $composer\10.createNode((Function0)factory\12);
                } else {
                    $composer\10.useNode();
                }
                Composer composer2 = Updater.constructor-impl((Composer)$composer\10);
                boolean bl17 = false;
                Updater.set-impl((Composer)composer2, (Object)measurePolicy, (Function2)ComposeUiNode.Companion.getSetMeasurePolicy());
                Updater.set-impl((Composer)composer2, (Object)compositionLocalMap, (Function2)ComposeUiNode.Companion.getSetResolvedCompositionLocals());
                Function2 function2 = ComposeUiNode.Companion.getSetCompositeKeyHash();
                boolean bl18 = false;
                Composer composer3 = composer2;
                boolean bl19 = false;
                if (composer3.getInserting() || !Intrinsics.areEqual((Object)composer3.rememberedValue(), (Object)n23)) {
                    composer3.updateRememberedValue((Object)n23);
                    composer2.apply((Object)n23, function2);
                }
                Updater.set-impl((Composer)composer2, (Object)modifier3, (Function2)ComposeUiNode.Companion.getSetModifier());
                int n25 = 0xE & $changed\12 >> 6;
                void $composer\16 = $composer\10;
                boolean bl20 = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\16, (int)1833054614, (String)"C72@3469L9:Box.kt#2w3rfo");
                int n26 = 6 | 0x70 & $changed\10 >> 6;
                void var45_52 = $composer\16;
                BoxScope boxScope = (BoxScope)BoxScopeInstance.INSTANCE;
                boolean bl21 = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\17, (int)273139636, (String)"C1225@62000L1125,1251@63139L124,1343@66983L574,1358@67571L1068,1385@68653L1390:GroupActivity.kt#83vr7l");
                int n27 = 10;
                boolean bl22 = false;
                int n28 = 10;
                boolean bl23 = false;
                int n29 = 10;
                boolean bl24 = false;
                Modifier modifier4 = PaddingKt.padding-qDBjuR0$default((Modifier)boxScope.align((Modifier)Modifier.Companion, Alignment.Companion.getCenterStart()), (float)Dp.constructor-impl((float)n27), (float)Dp.constructor-impl((float)n28), (float)0.0f, (float)Dp.constructor-impl((float)n29), (int)4, null);
                if (compact) {
                    int n30 = 8;
                    boolean bl25 = false;
                    f = Dp.constructor-impl((float)n30);
                } else {
                    int objectArray22 = 9;
                    boolean bl26 = false;
                    f = Dp.constructor-impl((float)objectArray22);
                }
                Modifier modifier5 = ClipKt.clip((Modifier)SizeKt.fillMaxHeight$default((Modifier)SizeKt.width-3ABfNKs((Modifier)modifier4, (float)f), (float)0.0f, (int)1, null), (Shape)((Shape)ComposeUiKt.getNoMemoG2CapsuleShape()));
                if (isDark) {
                    objectArray22 = new Color[]{Color.box-impl((long)Color.copy-wmQWz5c$default((long)Color.Companion.getWhite-0d7_KjU(), (float)0.14f, (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null)), Color.box-impl((long)Color.copy-wmQWz5c$default((long)Color.Companion.getWhite-0d7_KjU(), (float)0.04f, (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null)), Color.box-impl((long)Color.copy-wmQWz5c$default((long)Color.Companion.getBlack-0d7_KjU(), (float)0.18f, (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null))};
                    list = CollectionsKt.listOf((Object[])objectArray22);
                } else {
                    objectArray22 = new Color[]{Color.box-impl((long)Color.copy-wmQWz5c$default((long)Color.Companion.getWhite-0d7_KjU(), (float)0.52f, (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null)), Color.box-impl((long)Color.copy-wmQWz5c$default((long)Color.Companion.getWhite-0d7_KjU(), (float)0.14f, (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null)), Color.box-impl((long)Color.copy-wmQWz5c$default((long)Color.Companion.getBlack-0d7_KjU(), (float)0.1f, (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null))};
                    list = CollectionsKt.listOf((Object[])objectArray22);
                }
                BoxKt.Box((Modifier)BackgroundKt.background$default((Modifier)modifier5, (Brush)Brush.Companion.verticalGradient-8A-3gB4$default((Brush.Companion)Brush.Companion, (List)list, (float)0.0f, (float)0.0f, (int)0, (int)14, null), null, (float)0.0f, (int)6, null), (Composer)$composer\17, (int)0);
                this.GroupAlbumPaperTexture(isDark, boxScope.matchParentSize((Modifier)Modifier.Companion), (Composer)$composer\17, 0x380 & $dirty >> 15, 0);
                if (!((Collection)previewRecords).isEmpty()) {
                    void other\36;
                    void modifier9;
                    Object object;
                    void $this$cache\32;
                    void other\29;
                    void arg0\29;
                    Object object2;
                    Function1 function1;
                    Modifier modifier6;
                    void $this$cache\25;
                    $composer\17.startReplaceGroup(274229843);
                    ComposerKt.sourceInformation((Composer)$composer\17, (String)"1263@63618L71,1257@63328L622,1277@64256L70,1271@63967L620");
                    int n31 = 18;
                    boolean bl27 = false;
                    int $this$dp\2622 = 18;
                    boolean $i$f$getDp\24\12622 = false;
                    Modifier modifier7 = SizeKt.fillMaxWidth((Modifier)SizeKt.fillMaxHeight((Modifier)PaddingKt.padding-qDBjuR0$default((Modifier)boxScope.align((Modifier)Modifier.Companion, Alignment.Companion.getCenterStart()), (float)Dp.constructor-impl((float)n31), (float)Dp.constructor-impl((float)$this$dp\2622), (float)0.0f, (float)0.0f, (int)12, null), (float)0.76f), (float)0.42f);
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer\17, (int)-960976565, (String)"CC(remember):GroupActivity.kt#9igjgp");
                    void $i$f$getDp\24\12622 = $composer\17;
                    boolean bl28 = false;
                    boolean bl29 = false;
                    Object object3 = $this$cache\25.rememberedValue();
                    boolean bl30 = false;
                    if (object3 == Composer.Companion.getEmpty()) {
                        modifier6 = modifier7;
                        boolean bl31 = false;
                        function1 = GroupActivity::GroupAlbumCoverCollage$lambda$275$lambda$266$lambda$265;
                        modifier7 = modifier6;
                        Function1 function12 = function1;
                        $this$cache\25.updateRememberedValue((Object)function12);
                        object2 = function12;
                    } else {
                        object2 = object3;
                    }
                    Function1 $this$dp\2622 = (Function1)object2;
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer\17);
                    float $this$dp\2622 = tileCorner;
                    int $this$dp\292 = 2;
                    boolean bl32 = false;
                    float $this$dp\292 = Dp.constructor-impl((float)$this$dp\292);
                    boolean bl33 = false;
                    BoxKt.Box((Modifier)BackgroundKt.background-bw27NRU$default((Modifier)ClipKt.clip((Modifier)GraphicsLayerModifierKt.graphicsLayer((Modifier)modifier7, (Function1)$this$dp\2622), (Shape)((Shape)ComposeUiKt.noMemoG2RoundedShape-0680j_4(Dp.constructor-impl((float)(arg0\29 + other\29))))), (long)(isDark ? Color.copy-wmQWz5c$default((long)Color.Companion.getWhite-0d7_KjU(), (float)0.06f, (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null) : Color.copy-wmQWz5c$default((long)Color.Companion.getWhite-0d7_KjU(), (float)0.32f, (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null)), null, (int)2, null), (Composer)$composer\17, (int)0);
                    int n32 = 16;
                    boolean bl34 = false;
                    int $this$dp\3322 = 18;
                    boolean $i$f$getDp\31\12762 = false;
                    Modifier modifier8 = SizeKt.fillMaxWidth((Modifier)SizeKt.fillMaxHeight((Modifier)PaddingKt.padding-qDBjuR0$default((Modifier)boxScope.align((Modifier)Modifier.Companion, Alignment.Companion.getCenterEnd()), (float)0.0f, (float)0.0f, (float)Dp.constructor-impl((float)n32), (float)Dp.constructor-impl((float)$this$dp\3322), (int)3, null), (float)0.58f), (float)0.3f);
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer\17, (int)-960956150, (String)"CC(remember):GroupActivity.kt#9igjgp");
                    void $i$f$getDp\31\12762 = $composer\17;
                    boolean bl35 = false;
                    boolean bl36 = false;
                    Object object4 = $this$cache\32.rememberedValue();
                    boolean bl37 = false;
                    if (object4 == Composer.Companion.getEmpty()) {
                        modifier6 = modifier8;
                        boolean bl38 = false;
                        function1 = GroupActivity::GroupAlbumCoverCollage$lambda$275$lambda$268$lambda$267;
                        modifier8 = modifier6;
                        Function1 function13 = function1;
                        $this$cache\32.updateRememberedValue((Object)function13);
                        object = function13;
                    } else {
                        object = object4;
                    }
                    Function1 $this$dp\3322 = (Function1)object;
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer\17);
                    float $this$dp\3322 = tileCorner;
                    boolean $this$dp\362 = true;
                    boolean bl39 = false;
                    float $this$dp\362 = Dp.constructor-impl((float)((float)$this$dp\362));
                    boolean bl40 = false;
                    BoxKt.Box((Modifier)BackgroundKt.background-bw27NRU$default((Modifier)ClipKt.clip((Modifier)GraphicsLayerModifierKt.graphicsLayer((Modifier)modifier8, (Function1)$this$dp\3322), (Shape)((Shape)ComposeUiKt.noMemoG2RoundedShape-0680j_4(Dp.constructor-impl((float)(modifier9 + other\36))))), (long)(isDark ? Color.copy-wmQWz5c$default((long)Color.Companion.getWhite-0d7_KjU(), (float)0.05f, (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null) : Color.copy-wmQWz5c$default((long)Color.Companion.getWhite-0d7_KjU(), (float)0.24f, (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null)), null, (int)2, null), (Composer)$composer\17, (int)0);
                    $composer\17.endReplaceGroup();
                } else {
                    $composer\17.startReplaceGroup(211385310);
                    $composer\17.endReplaceGroup();
                }
                if (previewRecords.isEmpty()) {
                    float f6;
                    $composer\17.startReplaceGroup(275534323);
                    ComposerKt.sourceInformation((Composer)$composer\17, (String)"1288@64663L619");
                    Modifier modifier9 = BackgroundKt.background$default((Modifier)SizeKt.fillMaxSize$default((Modifier)((Modifier)Modifier.Companion), (float)0.0f, (int)1, null), (Brush)this.groupAlbumCoverBrush(albumId, isDark), null, (float)0.0f, (int)6, null);
                    void bl36 = $composer\17;
                    boolean object4 = false;
                    boolean bl41 = false;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer\37, (int)1042775818, (String)"CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo");
                    horizontal = Alignment.Companion.getTopStart();
                    boolean bl42 = false;
                    MeasurePolicy measurePolicy2 = BoxKt.maybeCachedBoxMeasurePolicy((Alignment)horizontal, (boolean)bl42);
                    Modifier modifier10 = modifier2;
                    int n33 = 0x70 & n12 << 3;
                    boolean bl43 = false;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer\37, (int)-1159599143, (String)"CC(Layout)P(!1,2)80@3267L27,83@3433L360:Layout.kt#80mrfh");
                    int n34 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode((Composer)$composer\37, (int)0));
                    CompositionLocalMap compositionLocalMap2 = $composer\37.getCurrentCompositionLocalMap();
                    Modifier modifier11 = ComposedModifierKt.materializeModifier((Composer)$composer\37, (Modifier)modifier\38);
                    Function0 function06 = ComposeUiNode.Companion.getConstructor();
                    int n35 = 6 | 0x380 & n11 << 6;
                    boolean bl44 = false;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer\37, (int)-553112988, (String)"CC(ReusableComposeNode)N(factory,update,content)399@15590L9:Composables.kt#9igjgp");
                    if (!($composer\37.getApplier() instanceof Applier)) {
                        ComposablesKt.invalidApplier();
                    }
                    $composer\37.startReusableNode();
                    if ($composer\37.getInserting()) {
                        $composer\37.createNode(function04);
                    } else {
                        $composer\37.useNode();
                    }
                    Composer composer4 = Updater.constructor-impl((Composer)$composer\37);
                    boolean bl45 = false;
                    Updater.set-impl((Composer)composer4, (Object)measurePolicy2, (Function2)ComposeUiNode.Companion.getSetMeasurePolicy());
                    Updater.set-impl((Composer)composer4, (Object)compositionLocalMap2, (Function2)ComposeUiNode.Companion.getSetResolvedCompositionLocals());
                    Function2 function22 = ComposeUiNode.Companion.getSetCompositeKeyHash();
                    boolean bl46 = false;
                    Composer composer5 = composer4;
                    boolean bl47 = false;
                    if (composer5.getInserting() || !Intrinsics.areEqual((Object)composer5.rememberedValue(), (Object)n34)) {
                        composer5.updateRememberedValue((Object)n34);
                        composer4.apply((Object)n34, function22);
                    }
                    Updater.set-impl((Composer)composer4, (Object)modifier11, (Function2)ComposeUiNode.Companion.getSetModifier());
                    int n36 = 0xE & n10 >> 6;
                    void $composer\43 = $composer\37;
                    boolean bl48 = false;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer\43, (int)1833054614, (String)"C72@3469L9:Box.kt#2w3rfo");
                    int n37 = 6 | 0x70 & n12 >> 6;
                    void var76_114 = $composer\43;
                    BoxScope boxScope2 = (BoxScope)BoxScopeInstance.INSTANCE;
                    boolean bl49 = false;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer\44, (int)898632085, (String)"C1294@64902L39,1293@64862L402:GroupActivity.kt#83vr7l");
                    painter = PainterResources_androidKt.painterResource((int)R.drawable.ic_nm_group, (Composer)$composer\44, (int)0);
                    long l = Color.copy-wmQWz5c$default((long)Color.Companion.getWhite-0d7_KjU(), (float)(isDark ? 0.92f : 0.86f), (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null);
                    Modifier modifier12 = boxScope2.align((Modifier)Modifier.Companion, Alignment.Companion.getCenter());
                    if (compact) {
                        int n38 = 28;
                        boolean bl50 = false;
                        f6 = Dp.constructor-impl((float)n38);
                    } else {
                        n9 = 32;
                        boolean bl51 = false;
                        f6 = Dp.constructor-impl((float)n9);
                    }
                    Modifier modifier13 = SizeKt.size-3ABfNKs((Modifier)modifier12, (float)f6);
                    IconKt.Icon-ww6aTOc((Painter)painter, null, (Modifier)modifier13, (long)l, (Composer)$composer\44, (int)48, (int)0);
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer\44);
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer\43);
                    $composer\37.endNode();
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer\37);
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer\37);
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer\37);
                    $composer\17.endReplaceGroup();
                } else {
                    void modifier\55;
                    void $changed\55;
                    void verticalArrangement\55;
                    void horizontalArrangement\47;
                    $composer\17.startReplaceGroup(276217563);
                    ComposerKt.sourceInformation((Composer)$composer\17, (String)"1303@65320L1635");
                    modifier2 = PaddingKt.padding-3ABfNKs((Modifier)SizeKt.fillMaxSize$default((Modifier)((Modifier)Modifier.Companion), (float)0.0f, (int)1, null), (float)collagePadding);
                    horizontal = (Arrangement.Horizontal)Arrangement.INSTANCE.spacedBy-0680j_4(tileGap);
                    $composer\37 = $composer\17;
                    n12 = 48;
                    boolean bl52 = false;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer\47, (int)844473419, (String)"CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo");
                    Alignment.Vertical vertical2 = Alignment.Companion.getTop();
                    MeasurePolicy measurePolicy3 = RowKt.rowMeasurePolicy((Arrangement.Horizontal)horizontalArrangement\47, (Alignment.Vertical)vertical2, (Composer)$composer\47, (int)(0xE & bl2 >> 3 | 0x70 & bl2 >> 3));
                    modifier\38 = objectArray;
                    n11 = 0x70 & bl2 << 3;
                    boolean bl53 = false;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer\47, (int)-1159599143, (String)"CC(Layout)P(!1,2)80@3267L27,83@3433L360:Layout.kt#80mrfh");
                    int n39 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode((Composer)$composer\47, (int)0));
                    CompositionLocalMap compositionLocalMap3 = $composer\47.getCurrentCompositionLocalMap();
                    Modifier modifier14 = ComposedModifierKt.materializeModifier((Composer)$composer\47, (Modifier)modifier\48);
                    function04 = ComposeUiNode.Companion.getConstructor();
                    n10 = 6 | 0x380 & n8 << 6;
                    boolean bl54 = false;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer\47, (int)-553112988, (String)"CC(ReusableComposeNode)N(factory,update,content)399@15590L9:Composables.kt#9igjgp");
                    if (!($composer\47.getApplier() instanceof Applier)) {
                        ComposablesKt.invalidApplier();
                    }
                    $composer\47.startReusableNode();
                    if ($composer\47.getInserting()) {
                        $composer\47.createNode(function03);
                    } else {
                        $composer\47.useNode();
                    }
                    Composer composer6 = Updater.constructor-impl((Composer)$composer\47);
                    boolean bl55 = false;
                    Updater.set-impl((Composer)composer6, (Object)measurePolicy3, (Function2)ComposeUiNode.Companion.getSetMeasurePolicy());
                    Updater.set-impl((Composer)composer6, (Object)compositionLocalMap3, (Function2)ComposeUiNode.Companion.getSetResolvedCompositionLocals());
                    Function2 function23 = ComposeUiNode.Companion.getSetCompositeKeyHash();
                    boolean bl56 = false;
                    Composer composer7 = composer6;
                    boolean bl57 = false;
                    if (composer7.getInserting() || !Intrinsics.areEqual((Object)composer7.rememberedValue(), (Object)n39)) {
                        composer7.updateRememberedValue((Object)n39);
                        composer6.apply((Object)n39, function23);
                    }
                    Updater.set-impl((Composer)composer6, (Object)modifier14, (Function2)ComposeUiNode.Companion.getSetModifier());
                    int n40 = 0xE & n7 >> 6;
                    void $composer\53 = $composer\47;
                    boolean bl58 = false;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer\53, (int)1456264949, (String)"C101@5233L9:Row.kt#2w3rfo");
                    int n41 = 6 | 0x70 & bl2 >> 6;
                    $composer\44 = $composer\53;
                    RowScope rowScope = (RowScope)RowScopeInstance.INSTANCE;
                    boolean bl59 = false;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer\54, (int)118571111, (String)"C1309@65568L330,1317@65919L1018:GroupActivity.kt#83vr7l");
                    this.GroupAlbumCoverTile-DzVHIIc((MemoryRecord)CollectionsKt.getOrNull(previewRecords, (int)0), tileCorner, -2.8f, SizeKt.fillMaxHeight$default((Modifier)RowScope.weight$default((RowScope)rowScope, (Modifier)((Modifier)Modifier.Companion), (float)1.12f, (boolean)false, (int)2, null), (float)0.0f, (int)1, null), (Composer)$composer\54, 0xE000 & $dirty >> 9, 0);
                    painter = SizeKt.fillMaxHeight$default((Modifier)RowScope.weight$default((RowScope)rowScope, (Modifier)((Modifier)Modifier.Companion), (float)0.88f, (boolean)false, (int)2, null), (float)0.0f, (int)1, null);
                    Arrangement.Vertical vertical3 = (Arrangement.Vertical)Arrangement.INSTANCE.spacedBy-0680j_4(tileGap);
                    void var86_130 = $composer\54;
                    n9 = 48;
                    n6 = 0;
                    ComposerKt.sourceInformationMarkerStart((Composer)vertical, (int)1341605231, (String)"CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo");
                    Alignment.Horizontal horizontal2 = Alignment.Companion.getStart();
                    MeasurePolicy measurePolicy4 = ColumnKt.columnMeasurePolicy((Arrangement.Vertical)verticalArrangement\55, (Alignment.Horizontal)horizontal2, (Composer)vertical, (int)(0xE & $changed\55 >> 3 | 0x70 & $changed\55 >> 3));
                    void var88_132 = modifier\55;
                    int n42 = 0x70 & $changed\55 << 3;
                    boolean bl60 = false;
                    ComposerKt.sourceInformationMarkerStart((Composer)vertical, (int)-1159599143, (String)"CC(Layout)P(!1,2)80@3267L27,83@3433L360:Layout.kt#80mrfh");
                    int n43 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode((Composer)vertical, (int)0));
                    CompositionLocalMap compositionLocalMap4 = vertical.getCurrentCompositionLocalMap();
                    Modifier modifier15 = ComposedModifierKt.materializeModifier((Composer)vertical, (Modifier)modifier\56);
                    Function0 function07 = ComposeUiNode.Companion.getConstructor();
                    int n44 = 6 | 0x380 & n5 << 6;
                    boolean bl61 = false;
                    ComposerKt.sourceInformationMarkerStart((Composer)vertical, (int)-553112988, (String)"CC(ReusableComposeNode)N(factory,update,content)399@15590L9:Composables.kt#9igjgp");
                    if (!(vertical.getApplier() instanceof Applier)) {
                        ComposablesKt.invalidApplier();
                    }
                    vertical.startReusableNode();
                    if (vertical.getInserting()) {
                        vertical.createNode(function02);
                    } else {
                        vertical.useNode();
                    }
                    Composer composer8 = Updater.constructor-impl((Composer)vertical);
                    boolean bl62 = false;
                    Updater.set-impl((Composer)composer8, (Object)measurePolicy4, (Function2)ComposeUiNode.Companion.getSetMeasurePolicy());
                    Updater.set-impl((Composer)composer8, (Object)compositionLocalMap4, (Function2)ComposeUiNode.Companion.getSetResolvedCompositionLocals());
                    Function2 function24 = ComposeUiNode.Companion.getSetCompositeKeyHash();
                    boolean bl63 = false;
                    Composer composer9 = composer8;
                    boolean bl64 = false;
                    if (composer9.getInserting() || !Intrinsics.areEqual((Object)composer9.rememberedValue(), (Object)n43)) {
                        composer9.updateRememberedValue((Object)n43);
                        composer8.apply((Object)n43, function24);
                    }
                    Updater.set-impl((Composer)composer8, (Object)modifier15, (Function2)ComposeUiNode.Companion.getSetModifier());
                    int n45 = 0xE & n4 >> 6;
                    void $composer\61 = vertical;
                    boolean bl65 = false;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer\61, (int)2093002350, (String)"C89@4557L9:Column.kt#2w3rfo");
                    int n46 = 6 | 0x70 & $changed\55 >> 6;
                    void var107_151 = $composer\61;
                    ColumnScope columnScope = (ColumnScope)ColumnScopeInstance.INSTANCE;
                    boolean bl66 = false;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer\62, (int)1178115951, (String)"C1323@66184L353,1331@66562L353:GroupActivity.kt#83vr7l");
                    this.GroupAlbumCoverTile-DzVHIIc((MemoryRecord)CollectionsKt.getOrNull(previewRecords, (int)1), tileCorner, 2.2f, SizeKt.fillMaxWidth$default((Modifier)ColumnScope.weight$default((ColumnScope)columnScope, (Modifier)((Modifier)Modifier.Companion), (float)1.0f, (boolean)false, (int)2, null), (float)0.0f, (int)1, null), (Composer)$composer\62, 0x180 | 0xE000 & $dirty >> 9, 0);
                    this.GroupAlbumCoverTile-DzVHIIc((MemoryRecord)CollectionsKt.getOrNull(previewRecords, (int)2), tileCorner, 1.2f, SizeKt.fillMaxWidth$default((Modifier)ColumnScope.weight$default((ColumnScope)columnScope, (Modifier)((Modifier)Modifier.Companion), (float)1.0f, (boolean)false, (int)2, null), (float)0.0f, (int)1, null), (Composer)$composer\62, 0x180 | 0xE000 & $dirty >> 9, 0);
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer\62);
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer\61);
                    vertical.endNode();
                    ComposerKt.sourceInformationMarkerEnd((Composer)vertical);
                    ComposerKt.sourceInformationMarkerEnd((Composer)vertical);
                    ComposerKt.sourceInformationMarkerEnd((Composer)vertical);
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer\54);
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer\53);
                    $composer\47.endNode();
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer\47);
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer\47);
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer\47);
                    $composer\17.endReplaceGroup();
                }
                objectArray = new Color[]{Color.box-impl((long)Color.Companion.getTransparent-0d7_KjU()), Color.box-impl((long)Color.Companion.getTransparent-0d7_KjU()), Color.box-impl((long)Color.copy-wmQWz5c$default((long)Color.Companion.getBlack-0d7_KjU(), (float)(isDark ? 0.18f : 0.12f), (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null)), Color.box-impl((long)Color.copy-wmQWz5c$default((long)Color.Companion.getBlack-0d7_KjU(), (float)(isDark ? 0.48f : 0.34f), (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null))};
                BoxKt.Box((Modifier)BackgroundKt.background$default((Modifier)boxScope.matchParentSize((Modifier)Modifier.Companion), (Brush)Brush.Companion.verticalGradient-8A-3gB4$default((Brush.Companion)Brush.Companion, (List)CollectionsKt.listOf((Object[])objectArray), (float)0.0f, (float)0.0f, (int)0, (int)14, null), null, (float)0.0f, (int)6, null), (Composer)$composer\17, (int)0);
                int n47 = 10;
                boolean bl67 = false;
                boolean bl68 = true;
                boolean bl69 = false;
                int n48 = 10;
                boolean bl70 = false;
                int $this$dp\672 = 5;
                boolean bl71 = false;
                Modifier $this$dp\672 = PaddingKt.padding-VpY3zN4((Modifier)BorderKt.border-xT4_qwU((Modifier)BackgroundKt.background-bw27NRU$default((Modifier)ClipKt.clip((Modifier)PaddingKt.padding-3ABfNKs((Modifier)boxScope.align((Modifier)Modifier.Companion, Alignment.Companion.getTopEnd()), (float)Dp.constructor-impl((float)n47)), (Shape)((Shape)ComposeUiKt.getNoMemoG2CapsuleShape())), (long)(isDark ? Color.copy-wmQWz5c$default((long)Color.Companion.getBlack-0d7_KjU(), (float)0.28f, (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null) : Color.copy-wmQWz5c$default((long)Color.Companion.getWhite-0d7_KjU(), (float)0.64f, (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null)), null, (int)2, null), (float)Dp.constructor-impl((float)((float)bl68)), (long)(isDark ? Color.copy-wmQWz5c$default((long)Color.Companion.getWhite-0d7_KjU(), (float)0.12f, (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null) : Color.copy-wmQWz5c$default((long)Color.Companion.getWhite-0d7_KjU(), (float)0.42f, (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null)), (Shape)((Shape)ComposeUiKt.getNoMemoG2CapsuleShape())), (float)Dp.constructor-impl((float)n48), (float)Dp.constructor-impl((float)$this$dp\672));
                $composer\47 = $composer\17;
                bl2 = false;
                boolean bl72 = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\67, (int)1042775818, (String)"CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo");
                Alignment alignment2 = Alignment.Companion.getTopStart();
                boolean bl73 = false;
                MeasurePolicy measurePolicy5 = BoxKt.maybeCachedBoxMeasurePolicy((Alignment)alignment2, (boolean)bl73);
                modifier\48 = modifier\67;
                n8 = 0x70 & bl << 3;
                boolean bl74 = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\67, (int)-1159599143, (String)"CC(Layout)P(!1,2)80@3267L27,83@3433L360:Layout.kt#80mrfh");
                int n49 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode((Composer)$composer\67, (int)0));
                CompositionLocalMap compositionLocalMap5 = $composer\67.getCurrentCompositionLocalMap();
                Modifier modifier16 = ComposedModifierKt.materializeModifier((Composer)$composer\67, (Modifier)modifier\68);
                function03 = ComposeUiNode.Companion.getConstructor();
                n7 = 6 | 0x380 & n3 << 6;
                boolean bl75 = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\67, (int)-553112988, (String)"CC(ReusableComposeNode)N(factory,update,content)399@15590L9:Composables.kt#9igjgp");
                if (!($composer\67.getApplier() instanceof Applier)) {
                    ComposablesKt.invalidApplier();
                }
                $composer\67.startReusableNode();
                if ($composer\67.getInserting()) {
                    $composer\67.createNode(function0);
                } else {
                    $composer\67.useNode();
                }
                Composer composer10 = Updater.constructor-impl((Composer)$composer\67);
                boolean bl76 = false;
                Updater.set-impl((Composer)composer10, (Object)measurePolicy5, (Function2)ComposeUiNode.Companion.getSetMeasurePolicy());
                Updater.set-impl((Composer)composer10, (Object)compositionLocalMap5, (Function2)ComposeUiNode.Companion.getSetResolvedCompositionLocals());
                Function2 function25 = ComposeUiNode.Companion.getSetCompositeKeyHash();
                boolean bl77 = false;
                Composer composer11 = composer10;
                boolean bl78 = false;
                if (composer11.getInserting() || !Intrinsics.areEqual((Object)composer11.rememberedValue(), (Object)n49)) {
                    composer11.updateRememberedValue((Object)n49);
                    composer10.apply((Object)n49, function25);
                }
                Updater.set-impl((Composer)composer10, (Object)modifier16, (Function2)ComposeUiNode.Companion.getSetModifier());
                int n50 = 0xE & n2 >> 6;
                void $composer\73 = $composer\67;
                boolean bl79 = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\73, (int)1833054614, (String)"C72@3469L9:Box.kt#2w3rfo");
                int n51 = 6 | 0x70 & bl >> 6;
                $composer\54 = $composer\73;
                BoxScope boxScope3 = (BoxScope)BoxScopeInstance.INSTANCE;
                boolean bl80 = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\74, (int)1022433983, (String)"C1377@68367L258:GroupActivity.kt#83vr7l");
                TextKt.Text--4IGK_g((String)(memoryCount + "\u6761"), null, (long)(isDark ? Color.copy-wmQWz5c$default((long)Color.Companion.getWhite-0d7_KjU(), (float)0.95f, (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null) : palette.getTextPrimary-0d7_KjU()), (long)TextUnitKt.getSp((int)11), null, (FontWeight)FontWeight.Companion.getSemiBold(), null, (long)0L, null, null, (long)0L, (int)0, (boolean)false, (int)0, (int)0, null, null, (Composer)$composer\74, (int)199680, (int)0, (int)131026);
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\74);
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\73);
                $composer\67.endNode();
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\67);
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\67);
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\67);
                int n52 = 18;
                boolean bl81 = false;
                int n53 = 18;
                boolean bl82 = false;
                int $this$dp\782 = 16;
                boolean bl83 = false;
                Modifier $this$dp\782 = PaddingKt.padding-qDBjuR0$default((Modifier)boxScope.align((Modifier)Modifier.Companion, Alignment.Companion.getBottomStart()), (float)Dp.constructor-impl((float)n52), (float)0.0f, (float)Dp.constructor-impl((float)n53), (float)Dp.constructor-impl((float)$this$dp\782), (int)2, null);
                $composer\67 = $composer\17;
                bl = false;
                boolean bl84 = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\78, (int)1341605231, (String)"CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo");
                Arrangement.Vertical vertical4 = Arrangement.INSTANCE.getTop();
                Alignment.Horizontal horizontal3 = Alignment.Companion.getStart();
                MeasurePolicy measurePolicy6 = ColumnKt.columnMeasurePolicy((Arrangement.Vertical)vertical4, (Alignment.Horizontal)horizontal3, (Composer)$composer\78, (int)(0xE & $changed\78 >> 3 | 0x70 & $changed\78 >> 3));
                modifier\68 = modifier\78;
                n3 = 0x70 & $changed\78 << 3;
                boolean bl85 = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\78, (int)-1159599143, (String)"CC(Layout)P(!1,2)80@3267L27,83@3433L360:Layout.kt#80mrfh");
                int n54 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode((Composer)$composer\78, (int)0));
                CompositionLocalMap compositionLocalMap6 = $composer\78.getCurrentCompositionLocalMap();
                Modifier modifier17 = ComposedModifierKt.materializeModifier((Composer)$composer\78, (Modifier)modifier\79);
                function0 = ComposeUiNode.Companion.getConstructor();
                n2 = 6 | 0x380 & $changed\79 << 6;
                boolean bl86 = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\78, (int)-553112988, (String)"CC(ReusableComposeNode)N(factory,update,content)399@15590L9:Composables.kt#9igjgp");
                if (!($composer\78.getApplier() instanceof Applier)) {
                    ComposablesKt.invalidApplier();
                }
                $composer\78.startReusableNode();
                if ($composer\78.getInserting()) {
                    void factory\80;
                    $composer\78.createNode((Function0)factory\80);
                } else {
                    $composer\78.useNode();
                }
                Composer composer12 = Updater.constructor-impl((Composer)$composer\78);
                boolean bl87 = false;
                Updater.set-impl((Composer)composer12, (Object)measurePolicy6, (Function2)ComposeUiNode.Companion.getSetMeasurePolicy());
                Updater.set-impl((Composer)composer12, (Object)compositionLocalMap6, (Function2)ComposeUiNode.Companion.getSetResolvedCompositionLocals());
                Function2 function26 = ComposeUiNode.Companion.getSetCompositeKeyHash();
                boolean bl88 = false;
                Composer composer13 = composer12;
                boolean bl89 = false;
                if (composer13.getInserting() || !Intrinsics.areEqual((Object)composer13.rememberedValue(), (Object)n54)) {
                    composer13.updateRememberedValue((Object)n54);
                    composer12.apply((Object)n54, function26);
                }
                Updater.set-impl((Composer)composer12, (Object)modifier17, (Function2)ComposeUiNode.Companion.getSetModifier());
                int n55 = 0xE & $changed\80 >> 6;
                void $composer\84 = $composer\78;
                boolean bl90 = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\84, (int)2093002350, (String)"C89@4557L9:Column.kt#2w3rfo");
                int n56 = 6 | 0x70 & $changed\78 >> 6;
                $composer\74 = $composer\84;
                ColumnScope columnScope = (ColumnScope)ColumnScopeInstance.INSTANCE;
                boolean bl91 = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\85, (int)-2046092656, (String)"C1390@68852L114,1394@68983L1046:GroupActivity.kt#83vr7l");
                this.GroupAlbumFoilTitle(albumName, compact, null, (Composer)$composer\85, 0xE & $dirty >> 3 | 0x70 & $dirty >> 12 | 0x1C00 & $dirty >> 12, 4);
                int $this$dp\872 = 5;
                boolean bl92 = false;
                Modifier $this$dp\872 = PaddingKt.padding-qDBjuR0$default((Modifier)((Modifier)Modifier.Companion), (float)0.0f, (float)Dp.constructor-impl((float)$this$dp\872), (float)0.0f, (float)0.0f, (int)13, null);
                vertical = Alignment.Companion.getCenterVertically();
                void $changed\55 = $composer\85;
                n6 = 390;
                boolean bl93 = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\87, (int)844473419, (String)"CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo");
                Arrangement.Horizontal horizontal4 = Arrangement.INSTANCE.getStart();
                MeasurePolicy measurePolicy7 = RowKt.rowMeasurePolicy((Arrangement.Horizontal)horizontal4, (Alignment.Vertical)verticalAlignment\87, (Composer)$composer\87, (int)(0xE & $changed\87 >> 3 | 0x70 & $changed\87 >> 3));
                modifier\56 = modifier\87;
                n5 = 0x70 & $changed\87 << 3;
                boolean bl94 = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\87, (int)-1159599143, (String)"CC(Layout)P(!1,2)80@3267L27,83@3433L360:Layout.kt#80mrfh");
                int n57 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode((Composer)$composer\87, (int)0));
                CompositionLocalMap compositionLocalMap7 = $composer\87.getCurrentCompositionLocalMap();
                Modifier modifier18 = ComposedModifierKt.materializeModifier((Composer)$composer\87, (Modifier)modifier\88);
                function02 = ComposeUiNode.Companion.getConstructor();
                n4 = 6 | 0x380 & $changed\88 << 6;
                boolean bl95 = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\87, (int)-553112988, (String)"CC(ReusableComposeNode)N(factory,update,content)399@15590L9:Composables.kt#9igjgp");
                if (!($composer\87.getApplier() instanceof Applier)) {
                    ComposablesKt.invalidApplier();
                }
                $composer\87.startReusableNode();
                if ($composer\87.getInserting()) {
                    void factory\89;
                    $composer\87.createNode((Function0)factory\89);
                } else {
                    $composer\87.useNode();
                }
                Composer composer14 = Updater.constructor-impl((Composer)$composer\87);
                boolean bl96 = false;
                Updater.set-impl((Composer)composer14, (Object)measurePolicy7, (Function2)ComposeUiNode.Companion.getSetMeasurePolicy());
                Updater.set-impl((Composer)composer14, (Object)compositionLocalMap7, (Function2)ComposeUiNode.Companion.getSetResolvedCompositionLocals());
                Function2 function27 = ComposeUiNode.Companion.getSetCompositeKeyHash();
                boolean bl97 = false;
                Composer composer15 = composer14;
                boolean bl98 = false;
                if (composer15.getInserting() || !Intrinsics.areEqual((Object)composer15.rememberedValue(), (Object)n57)) {
                    composer15.updateRememberedValue((Object)n57);
                    composer14.apply((Object)n57, function27);
                }
                Updater.set-impl((Composer)composer14, (Object)modifier18, (Function2)ComposeUiNode.Companion.getSetModifier());
                int n58 = 0xE & $changed\89 >> 6;
                void $composer\93 = $composer\87;
                boolean bl99 = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\93, (int)1456264949, (String)"C101@5233L9:Row.kt#2w3rfo");
                int n59 = 6 | 0x70 & $changed\87 >> 6;
                $composer\62 = $composer\93;
                RowScope rowScope = (RowScope)RowScopeInstance.INSTANCE;
                boolean bl100 = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\94, (int)-1275721170, (String)"C1398@69156L243,1404@69420L39,1405@69480L239,1411@69740L39,1412@69800L211:GroupActivity.kt#83vr7l");
                TextKt.Text--4IGK_g((String)(memoryCount + "\u6761\u8bb0\u5fc6"), null, (long)Color.copy-wmQWz5c$default((long)Color.Companion.getWhite-0d7_KjU(), (float)0.88f, (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null), (long)TextUnitKt.getSp((int)12), null, (FontWeight)FontWeight.Companion.getSemiBold(), null, (long)0L, null, null, (long)0L, (int)0, (boolean)false, (int)0, (int)0, null, null, (Composer)$composer\94, (int)200064, (int)0, (int)131026);
                int n60 = 7;
                boolean bl101 = false;
                SpacerKt.Spacer((Modifier)SizeKt.width-3ABfNKs((Modifier)((Modifier)Modifier.Companion), (float)Dp.constructor-impl((float)n60)), (Composer)$composer\94, (int)6);
                int n61 = 3;
                boolean bl102 = false;
                BoxKt.Box((Modifier)BackgroundKt.background-bw27NRU$default((Modifier)ClipKt.clip((Modifier)SizeKt.size-3ABfNKs((Modifier)((Modifier)Modifier.Companion), (float)Dp.constructor-impl((float)n61)), (Shape)((Shape)ComposeUiKt.getNoMemoG2CapsuleShape())), (long)Color.copy-wmQWz5c$default((long)Color.Companion.getWhite-0d7_KjU(), (float)0.46f, (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null), null, (int)2, null), (Composer)$composer\94, (int)0);
                int n62 = 7;
                boolean bl103 = false;
                SpacerKt.Spacer((Modifier)SizeKt.width-3ABfNKs((Modifier)((Modifier)Modifier.Companion), (float)Dp.constructor-impl((float)n62)), (Composer)$composer\94, (int)6);
                TextKt.Text--4IGK_g((String)dayText, null, (long)Color.copy-wmQWz5c$default((long)Color.Companion.getWhite-0d7_KjU(), (float)0.74f, (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null), (long)TextUnitKt.getSp((int)12), null, null, null, (long)0L, null, null, (long)0L, (int)0, (boolean)false, (int)1, (int)0, null, null, (Composer)$composer\94, (int)(0xD80 | 0xE & $dirty >> 12), (int)3072, (int)122866);
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\94);
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\93);
                $composer\87.endNode();
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\87);
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\87);
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\87);
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\85);
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\84);
                $composer\78.endNode();
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\78);
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\78);
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\78);
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\17);
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\16);
                $composer\10.endNode();
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\10);
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\10);
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\10);
                if (ComposerKt.isTraceInProgress()) {
                    ComposerKt.traceEventEnd();
                }
            } else {
                $composer.skipToGroupEnd();
            }
            ScopeUpdateScope scopeUpdateScope2 = scopeUpdateScope = $composer.endRestartGroup();
            if (scopeUpdateScope2 == null) break block65;
            scopeUpdateScope2.updateScope((arg_0, arg_1) -> GroupActivity.GroupAlbumCoverCollage$lambda$276(this, albumId, albumName, previewRecords, memoryCount, dayText, compact, modifier, $changed, n, arg_0, arg_1));
        }
    }

    /*
     * WARNING - void declaration
     */
    @Composable
    @ComposableTarget(applier="androidx.compose.ui.UiComposable")
    private final void GroupAlbumFoilTitle(String text, boolean compact, Modifier modifier, Composer $composer, int $changed, int n) {
        block14: {
            $composer = $composer.startRestartGroup(2011273345);
            ComposerKt.sourceInformation((Composer)$composer, (String)"C(GroupAlbumFoilTitle)N(text,compact,modifier)1430@70270L1294:GroupActivity.kt#83vr7l");
            int $dirty = $changed;
            if (($changed & 6) == 0) {
                $dirty |= $composer.changed((Object)text) ? 4 : 2;
            }
            if (($changed & 0x30) == 0) {
                $dirty |= $composer.changed(compact) ? 32 : 16;
            }
            if ((n & 4) != 0) {
                $dirty |= 0x180;
            } else if (($changed & 0x180) == 0) {
                $dirty |= $composer.changed((Object)modifier) ? 256 : 128;
            }
            if ($composer.shouldExecute(($dirty & 0x93) != 146, $dirty & 1)) {
                void x\13;
                void $composer\8;
                void $changed\3;
                void $changed\2;
                void modifier\2;
                void modifier\1;
                void $composer\1;
                if ((n & 4) != 0) {
                    modifier = (Modifier)Modifier.Companion;
                }
                if (ComposerKt.isTraceInProgress()) {
                    ComposerKt.traceEventStart((int)2011273345, (int)$dirty, (int)-1, (String)"com.han.nomemo.GroupActivity.GroupAlbumFoilTitle (GroupActivity.kt:1428)");
                }
                long titleSize = compact ? TextUnitKt.getSp((int)18) : TextUnitKt.getSp((int)19);
                Modifier modifier2 = modifier;
                Composer composer = $composer;
                int n2 = 0xE & $dirty >> 6;
                boolean bl = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\1, (int)1042775818, (String)"CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo");
                Alignment alignment = Alignment.Companion.getTopStart();
                boolean bl2 = false;
                MeasurePolicy measurePolicy = BoxKt.maybeCachedBoxMeasurePolicy((Alignment)alignment, (boolean)bl2);
                void var17_16 = modifier\1;
                int n3 = 0x70 & n2 << 3;
                boolean bl3 = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\1, (int)-1159599143, (String)"CC(Layout)P(!1,2)80@3267L27,83@3433L360:Layout.kt#80mrfh");
                int n4 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode((Composer)$composer\1, (int)0));
                CompositionLocalMap compositionLocalMap = $composer\1.getCurrentCompositionLocalMap();
                Modifier modifier3 = ComposedModifierKt.materializeModifier((Composer)$composer\1, (Modifier)modifier\2);
                Function0 function0 = ComposeUiNode.Companion.getConstructor();
                int n5 = 6 | 0x380 & $changed\2 << 6;
                boolean bl4 = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\1, (int)-553112988, (String)"CC(ReusableComposeNode)N(factory,update,content)399@15590L9:Composables.kt#9igjgp");
                if (!($composer\1.getApplier() instanceof Applier)) {
                    ComposablesKt.invalidApplier();
                }
                $composer\1.startReusableNode();
                if ($composer\1.getInserting()) {
                    void factory\3;
                    $composer\1.createNode((Function0)factory\3);
                } else {
                    $composer\1.useNode();
                }
                Composer composer2 = Updater.constructor-impl((Composer)$composer\1);
                boolean bl5 = false;
                Updater.set-impl((Composer)composer2, (Object)measurePolicy, (Function2)ComposeUiNode.Companion.getSetMeasurePolicy());
                Updater.set-impl((Composer)composer2, (Object)compositionLocalMap, (Function2)ComposeUiNode.Companion.getSetResolvedCompositionLocals());
                Function2 function2 = ComposeUiNode.Companion.getSetCompositeKeyHash();
                boolean bl6 = false;
                Composer composer3 = composer2;
                boolean bl7 = false;
                if (composer3.getInserting() || !Intrinsics.areEqual((Object)composer3.rememberedValue(), (Object)n4)) {
                    composer3.updateRememberedValue((Object)n4);
                    composer2.apply((Object)n4, function2);
                }
                Updater.set-impl((Composer)composer2, (Object)modifier3, (Function2)ComposeUiNode.Companion.getSetModifier());
                int n6 = 0xE & $changed\3 >> 6;
                void $composer\7 = $composer\1;
                boolean bl8 = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\7, (int)1833054614, (String)"C72@3469L9:Box.kt#2w3rfo");
                int n7 = 6 | 0x70 & n2 >> 6;
                void var36_35 = $composer\7;
                BoxScope boxScope = (BoxScope)BoxScopeInstance.INSTANCE;
                boolean bl9 = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\8, (int)238315551, (String)"C1431@70309L336,1440@70658L342,1449@71013L541:GroupActivity.kt#83vr7l");
                long l = Color.copy-wmQWz5c$default((long)Color.Companion.getBlack-0d7_KjU(), (float)0.16f, (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null);
                FontWeight fontWeight = FontWeight.Companion.getBold();
                int n8 = TextOverflow.Companion.getEllipsis-gIe3tQ8();
                double d = 0.6;
                boolean bl10 = false;
                double d2 = 0.8;
                boolean bl11 = false;
                Modifier modifier4 = OffsetKt.offset-VpY3zN4((Modifier)((Modifier)Modifier.Companion), (float)Dp.constructor-impl((float)((float)d)), (float)Dp.constructor-impl((float)((float)d2)));
                TextKt.Text--4IGK_g((String)text, (Modifier)modifier4, (long)l, (long)titleSize, null, (FontWeight)fontWeight, null, (long)0L, null, null, (long)0L, (int)n8, (boolean)false, (int)1, (int)0, null, null, (Composer)$composer\8, (int)(0x301B0 | 0xE & $dirty), (int)3120, (int)120784);
                l = Color.copy-wmQWz5c$default((long)Color.Companion.getWhite-0d7_KjU(), (float)0.34f, (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null);
                fontWeight = FontWeight.Companion.getBold();
                n8 = TextOverflow.Companion.getEllipsis-gIe3tQ8();
                double d3 = -0.4;
                boolean bl12 = false;
                double d4 = -0.4;
                boolean bl13 = false;
                modifier4 = OffsetKt.offset-VpY3zN4((Modifier)((Modifier)Modifier.Companion), (float)Dp.constructor-impl((float)((float)d3)), (float)Dp.constructor-impl((float)((float)d4)));
                TextKt.Text--4IGK_g((String)text, (Modifier)modifier4, (long)l, (long)titleSize, null, (FontWeight)fontWeight, null, (long)0L, null, null, (long)0L, (int)n8, (boolean)false, (int)1, (int)0, null, null, (Composer)$composer\8, (int)(0x30180 | 0xE & $dirty), (int)3120, (int)120784);
                l = Color.copy-wmQWz5c$default((long)ColorKt.Color((long)4294375419L), (float)0.96f, (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null);
                fontWeight = FontWeight.Companion.getBold();
                n8 = TextOverflow.Companion.getEllipsis-gIe3tQ8();
                float f = 0.0f;
                float f2 = 0.0f;
                boolean bl14 = false;
                boolean bl15 = false;
                long l2 = Float.floatToRawIntBits((float)x\13);
                long l3 = Float.floatToRawIntBits(f2);
                modifier4 = new TextStyle(0L, 0L, null, null, null, null, null, 0L, null, null, null, 0L, null, new Shadow(Color.copy-wmQWz5c$default((long)Color.Companion.getWhite-0d7_KjU(), (float)0.18f, (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null), Offset.constructor-impl((long)(l2 << 32 | l3 & 0xFFFFFFFFL)), 7.0f, null), null, 0, 0, 0L, null, null, null, 0, 0, null, 0xFFDFFF, null);
                TextKt.Text--4IGK_g((String)text, null, (long)l, (long)titleSize, null, (FontWeight)fontWeight, null, (long)0L, null, null, (long)0L, (int)n8, (boolean)false, (int)1, (int)0, null, (TextStyle)modifier4, (Composer)$composer\8, (int)(0x30180 | 0xE & $dirty), (int)1575984, (int)55250);
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\8);
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\7);
                $composer\1.endNode();
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\1);
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\1);
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\1);
                if (ComposerKt.isTraceInProgress()) {
                    ComposerKt.traceEventEnd();
                }
            } else {
                $composer.skipToGroupEnd();
            }
            ScopeUpdateScope scopeUpdateScope = $composer.endRestartGroup();
            if (scopeUpdateScope == null) break block14;
            scopeUpdateScope.updateScope((arg_0, arg_1) -> GroupActivity.GroupAlbumFoilTitle$lambda$278(this, text, compact, modifier, $changed, n, arg_0, arg_1));
        }
    }

    /*
     * WARNING - void declaration
     */
    @Composable
    @ComposableTarget(applier="androidx.compose.ui.UiComposable")
    private final void GroupAlbumPaperTexture(boolean isDark, Modifier modifier, Composer $composer, int $changed, int n) {
        block11: {
            $composer = $composer.startRestartGroup(-1317004261);
            ComposerKt.sourceInformation((Composer)$composer, (String)"C(GroupAlbumPaperTexture)N(isDark,modifier)1494@72362L1517,1493@72311L1578:GroupActivity.kt#83vr7l");
            int $dirty = $changed;
            if (($changed & 6) == 0) {
                $dirty |= $composer.changed(isDark) ? 4 : 2;
            }
            if ((n & 2) != 0) {
                $dirty |= 0x30;
            } else if (($changed & 0x30) == 0) {
                $dirty |= $composer.changed((Object)modifier) ? 32 : 16;
            }
            if ($composer.shouldExecute(($dirty & 0x13) != 18, $dirty & 1)) {
                Object object;
                void $this$cache\1;
                if ((n & 2) != 0) {
                    modifier = (Modifier)Modifier.Companion;
                }
                if (ComposerKt.isTraceInProgress()) {
                    ComposerKt.traceEventStart((int)-1317004261, (int)$dirty, (int)-1, (String)"com.han.nomemo.GroupActivity.GroupAlbumPaperTexture (GroupActivity.kt:1471)");
                }
                long verticalLineColor = isDark ? Color.copy-wmQWz5c$default((long)Color.Companion.getWhite-0d7_KjU(), (float)0.026f, (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null) : Color.copy-wmQWz5c$default((long)Color.Companion.getWhite-0d7_KjU(), (float)0.18f, (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null);
                long diagonalLineColor = isDark ? Color.copy-wmQWz5c$default((long)Color.Companion.getBlack-0d7_KjU(), (float)0.12f, (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null) : ColorKt.Color((int)0x14000000);
                long topSheen = isDark ? Color.copy-wmQWz5c$default((long)Color.Companion.getWhite-0d7_KjU(), (float)0.055f, (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null) : Color.copy-wmQWz5c$default((long)Color.Companion.getWhite-0d7_KjU(), (float)0.32f, (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null);
                long bottomShade = isDark ? Color.copy-wmQWz5c$default((long)Color.Companion.getBlack-0d7_KjU(), (float)0.16f, (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null) : ColorKt.Color((int)0x12000000);
                Modifier modifier2 = modifier;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)1374284168, (String)"CC(remember):GroupActivity.kt#9igjgp");
                Composer composer = $composer;
                boolean bl = $composer.changed(topSheen) | $composer.changed(bottomShade) | $composer.changed(verticalLineColor) | $composer.changed(diagonalLineColor);
                boolean bl2 = false;
                Object object2 = $this$cache\1.rememberedValue();
                boolean bl3 = false;
                if (bl || object2 == Composer.Companion.getEmpty()) {
                    Modifier modifier3 = modifier2;
                    boolean bl4 = false;
                    modifier2 = modifier3;
                    Function1 function1 = arg_0 -> GroupActivity.GroupAlbumPaperTexture$lambda$281$lambda$280(topSheen, bottomShade, verticalLineColor, diagonalLineColor, arg_0);
                    $this$cache\1.updateRememberedValue((Object)function1);
                    object = function1;
                } else {
                    object = object2;
                }
                Function1 function1 = (Function1)object;
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
                BoxKt.Box((Modifier)DrawModifierKt.drawWithCache((Modifier)modifier2, (Function1)function1), (Composer)$composer, (int)0);
                if (ComposerKt.isTraceInProgress()) {
                    ComposerKt.traceEventEnd();
                }
            } else {
                $composer.skipToGroupEnd();
            }
            ScopeUpdateScope scopeUpdateScope = $composer.endRestartGroup();
            if (scopeUpdateScope == null) break block11;
            scopeUpdateScope.updateScope((arg_0, arg_1) -> GroupActivity.GroupAlbumPaperTexture$lambda$282(this, isDark, modifier, $changed, n, arg_0, arg_1));
        }
    }

    /*
     * WARNING - void declaration
     */
    @Composable
    @ComposableTarget(applier="androidx.compose.ui.UiComposable")
    private final void GroupAlbumCoverTile-DzVHIIc(MemoryRecord record, float cornerRadius, float rotationZ, Modifier modifier, Composer $composer, int $changed, int n) {
        block16: {
            $composer = $composer.startRestartGroup(-2035392589);
            ComposerKt.sourceInformation((Composer)$composer, (String)"C(GroupAlbumCoverTile)N(record,cornerRadius:c#ui.unit.Dp,rotationZ,modifier)1545@74100L21,1546@74144L23,1566@74832L66,1582@75422L2133,1564@74749L2806:GroupActivity.kt#83vr7l");
            int $dirty = $changed;
            if (($changed & 6) == 0) {
                $dirty |= $composer.changedInstance((Object)record) ? 4 : 2;
            }
            if (($changed & 0x30) == 0) {
                $dirty |= $composer.changed(cornerRadius) ? 32 : 16;
            }
            if (($changed & 0x180) == 0) {
                $dirty |= $composer.changed(rotationZ) ? 256 : 128;
            }
            if ((n & 8) != 0) {
                $dirty |= 0xC00;
            } else if (($changed & 0xC00) == 0) {
                $dirty |= $composer.changed((Object)modifier) ? 2048 : 1024;
            }
            if (($changed & 0x6000) == 0) {
                $dirty |= $composer.changedInstance((Object)this) ? 16384 : 8192;
            }
            if ($composer.shouldExecute(($dirty & 0x2493) != 9362, $dirty & 1)) {
                float f;
                Object object;
                void $this$cache\4;
                void other\2;
                void arg0\2;
                if ((n & 8) != 0) {
                    modifier = (Modifier)Modifier.Companion;
                }
                if (ComposerKt.isTraceInProgress()) {
                    ComposerKt.traceEventStart((int)-2035392589, (int)$dirty, (int)-1, (String)"com.han.nomemo.GroupActivity.GroupAlbumCoverTile (GroupActivity.kt:1544)");
                }
                boolean isDark = DarkThemeKt.isSystemInDarkTheme((Composer)$composer, (int)0);
                NoMemoPalette palette = ComposeUiKt.rememberNoMemoPalette($composer, 0);
                long frameSurface = isDark ? ComposeUiKt.noMemoCardSurfaceColor-4WTKRHQ(true, Color.copy-wmQWz5c$default((long)palette.getGlassFill-0d7_KjU(), (float)0.96f, (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null)) : Color.copy-wmQWz5c$default((long)Color.Companion.getWhite-0d7_KjU(), (float)0.97f, (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null);
                long frameBorder = isDark ? Color.copy-wmQWz5c$default((long)Color.Companion.getWhite-0d7_KjU(), (float)0.08f, (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null) : Color.copy-wmQWz5c$default((long)palette.getGlassStroke-0d7_KjU(), (float)0.78f, (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null);
                long fallbackBackground = isDark ? Color.copy-wmQWz5c$default((long)Color.Companion.getWhite-0d7_KjU(), (float)0.08f, (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null) : ColorKt.Color((long)4294244090L);
                float f2 = cornerRadius;
                int $this$dp\22 = 3;
                boolean bl = false;
                float $this$dp\22 = Dp.constructor-impl((float)$this$dp\22);
                boolean bl2 = false;
                int $this$dp\42 = 10;
                boolean $i$f$getDp\3\15642 = false;
                float innerCorner = ((Dp)RangesKt.coerceAtLeast((Comparable)Dp.box-impl((float)Dp.constructor-impl((float)(arg0\2 - other\2))), (Comparable)Dp.box-impl((float)Dp.constructor-impl((float)$this$dp\42)))).unbox-impl();
                Modifier modifier2 = modifier;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)-1802128491, (String)"CC(remember):GroupActivity.kt#9igjgp");
                Composer $i$f$getDp\3\15642 = $composer;
                boolean bl3 = ($dirty & 0x380) == 256;
                boolean bl4 = false;
                Object object2 = $this$cache\4.rememberedValue();
                boolean bl5 = false;
                if (bl3 || object2 == Composer.Companion.getEmpty()) {
                    Modifier modifier3 = modifier2;
                    boolean bl6 = false;
                    modifier2 = modifier3;
                    Function1 function1 = arg_0 -> GroupActivity.GroupAlbumCoverTile_DzVHIIc$lambda$284$lambda$283(rotationZ, arg_0);
                    $this$cache\4.updateRememberedValue((Object)function1);
                    object = function1;
                } else {
                    object = object2;
                }
                Function1 $this$dp\42 = (Function1)object;
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
                Modifier modifier4 = GraphicsLayerModifierKt.graphicsLayer((Modifier)modifier2, (Function1)$this$dp\42);
                if (isDark) {
                    int n2 = 6;
                    boolean bl7 = false;
                    f = Dp.constructor-impl((float)n2);
                } else {
                    int n3 = 10;
                    boolean bl8 = false;
                    f = Dp.constructor-impl((float)n3);
                }
                boolean bl9 = true;
                boolean bl10 = false;
                int n4 = 4;
                boolean bl11 = false;
                BoxWithConstraintsKt.BoxWithConstraints((Modifier)PaddingKt.padding-3ABfNKs((Modifier)BorderKt.border-xT4_qwU((Modifier)BackgroundKt.background-bw27NRU$default((Modifier)ClipKt.clip((Modifier)ShadowKt.shadow-s4CzXII$default((Modifier)modifier4, (float)f, (Shape)((Shape)ComposeUiKt.noMemoG2RoundedShape-0680j_4(cornerRadius)), (boolean)false, (long)0L, (long)0L, (int)24, null), (Shape)((Shape)ComposeUiKt.noMemoG2RoundedShape-0680j_4(cornerRadius))), (long)frameSurface, null, (int)2, null), (float)Dp.constructor-impl((float)((float)bl9)), (long)frameBorder, (Shape)((Shape)ComposeUiKt.noMemoG2RoundedShape-0680j_4(cornerRadius))), (float)Dp.constructor-impl((float)n4)), null, (boolean)false, (Function3)((Function3)ComposableLambdaKt.rememberComposableLambda((int)-1845516899, (boolean)true, (arg_0, arg_1, arg_2) -> GroupActivity.GroupAlbumCoverTile_DzVHIIc$lambda$286(record, innerCorner, fallbackBackground, this, isDark, arg_0, arg_1, arg_2), (Composer)$composer, (int)54)), (Composer)$composer, (int)3072, (int)6);
                if (ComposerKt.isTraceInProgress()) {
                    ComposerKt.traceEventEnd();
                }
            } else {
                $composer.skipToGroupEnd();
            }
            ScopeUpdateScope scopeUpdateScope = $composer.endRestartGroup();
            if (scopeUpdateScope == null) break block16;
            scopeUpdateScope.updateScope((arg_0, arg_1) -> GroupActivity.GroupAlbumCoverTile_DzVHIIc$lambda$287(this, record, cornerRadius, rotationZ, modifier, $changed, n, arg_0, arg_1));
        }
    }

    /*
     * WARNING - void declaration
     */
    @Composable
    @ComposableTarget(applier="androidx.compose.ui.UiComposable")
    private final void GroupAddExistingMemorySheet(BoxScope $this$GroupAddExistingMemorySheet, List<? extends MemoryRecord> records, Set<String> selectedRecordIds, String searchQuery, Function1<? super String, Unit> onSearchQueryChange, Function1<? super String, Unit> onToggleRecord, Function0<Unit> onDismiss, Function0<Boolean> onConfirm, Composer $composer, int $changed) {
        block33: {
            $composer = $composer.startRestartGroup(734431218);
            ComposerKt.sourceInformation((Composer)$composer, (String)"C(GroupAddExistingMemorySheet)N(records,selectedRecordIds,searchQuery,onSearchQueryChange,onToggleRecord,onDismiss,onConfirm)1645@77923L28,1646@77974L23,1647@78019L21,1660@78597L247,1667@78868L34,1668@78935L34,1670@79000L38,1670@78979L59,1674@79072L169,1674@79048L193,1682@79268L101,1688@79399L148,1695@79572L64,1697@79677L36,1697@79646L67,1701@79723L9405:GroupActivity.kt#83vr7l");
            int $dirty = $changed;
            if (($changed & 0x30) == 0) {
                $dirty |= $composer.changedInstance(records) ? 32 : 16;
            }
            if (($changed & 0x180) == 0) {
                $dirty |= $composer.changedInstance(selectedRecordIds) ? 256 : 128;
            }
            if (($changed & 0xC00) == 0) {
                $dirty |= $composer.changed((Object)searchQuery) ? 2048 : 1024;
            }
            if (($changed & 0x6000) == 0) {
                $dirty |= $composer.changedInstance(onSearchQueryChange) ? 16384 : 8192;
            }
            if (($changed & 0x30000) == 0) {
                $dirty |= $composer.changedInstance(onToggleRecord) ? 131072 : 65536;
            }
            if (($changed & 0x180000) == 0) {
                $dirty |= $composer.changedInstance(onDismiss) ? 0x100000 : 524288;
            }
            if (($changed & 0xC00000) == 0) {
                $dirty |= $composer.changedInstance(onConfirm) ? 0x800000 : 0x400000;
            }
            if ($composer.shouldExecute(($dirty & 0x492491) != 4793488, $dirty & 1)) {
                Object object;
                void $this$cache\36;
                Object object2;
                FiniteAnimationSpec finiteAnimationSpec;
                void $this$cache\33;
                void $composer\32;
                void $changed\27;
                void $changed\26;
                void modifier\26;
                void $changed\25;
                void modifier\25;
                void $composer\25;
                Object object3;
                Function0 function0;
                void $this$cache\22;
                Object object4;
                Function0 function02;
                Object object5;
                Function0 function03;
                Object object6;
                Function0 function04;
                Object object7;
                Object object8;
                Composer composer;
                Object object9;
                Composer composer2;
                Object object10;
                MutableState mutableState;
                if (ComposerKt.isTraceInProgress()) {
                    ComposerKt.traceEventStart((int)734431218, (int)$dirty, (int)-1, (String)"com.han.nomemo.GroupActivity.GroupAddExistingMemorySheet (GroupActivity.kt:1644)");
                }
                NoMemoAdaptiveSpec adaptive = ComposeUiKt.rememberNoMemoAdaptiveSpec($composer, 0);
                NoMemoPalette palette = ComposeUiKt.rememberNoMemoPalette($composer, 0);
                boolean isDark = DarkThemeKt.isSystemInDarkTheme((Composer)$composer, (int)0);
                long panelSurface = ComposeUiKt.noMemoThemeSyncedSheetSurface(palette, isDark);
                long searchSurface = ComposeUiKt.noMemoThemeSyncedContentSurface-t6yy7ic$default(palette, isDark, ComposeUiKt.noMemoCardSurfaceColor-4WTKRHQ(true, Color.copy-wmQWz5c$default((long)palette.getGlassFill-0d7_KjU(), (float)0.96f, (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null)), Color.copy-wmQWz5c$default((long)Color.Companion.getWhite-0d7_KjU(), (float)0.995f, (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null), 0.0f, 0.0f, 0.0f, 0.0f, 240, null);
                long dragHandleColor = isDark ? Color.copy-wmQWz5c$default((long)ColorKt.Color((long)4287532691L), (float)0.72f, (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null) : Color.copy-wmQWz5c$default((long)ColorKt.Color((long)4287532691L), (float)0.68f, (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null);
                int n = 620;
                boolean bl = false;
                int n2 = 700;
                boolean bl2 = false;
                int n3 = 340;
                boolean $i$f$getDp\3\16672 = false;
                float bodyHeight = ComposeUiKt.rememberNoMemoSheetHeight-kEYG_aY(Dp.constructor-impl((float)n), Dp.constructor-impl((float)n2), 0.86f, 0.82f, Dp.constructor-impl((float)n3), $composer, 28086, 0);
                ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)1413805620, (String)"CC(remember):GroupActivity.kt#9igjgp");
                Composer composer3 = $composer;
                boolean invalid\52 = false;
                boolean bl3 = false;
                Object object11 = mutableState.rememberedValue();
                boolean bl4 = false;
                if (object11 == Composer.Companion.getEmpty()) {
                    boolean bl5 = false;
                    MutableState mutableState2 = SnapshotStateKt.mutableStateOf$default((Object)false, null, (int)2, null);
                    mutableState.updateRememberedValue((Object)mutableState2);
                    object10 = mutableState2;
                } else {
                    object10 = object11;
                }
                MutableState $i$f$getDp\3\16672 = (MutableState)object10;
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
                MutableState visible$delegate = $i$f$getDp\3\16672;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)1413807764, (String)"CC(remember):GroupActivity.kt#9igjgp");
                Composer invalid\52 = $composer;
                boolean bl6 = false;
                boolean bl7 = false;
                Object object12 = composer2.rememberedValue();
                boolean bl8 = false;
                if (object12 == Composer.Companion.getEmpty()) {
                    boolean bl9 = false;
                    MutableState mutableState3 = SnapshotStateKt.mutableStateOf$default((Object)false, null, (int)2, null);
                    composer2.updateRememberedValue((Object)mutableState3);
                    object9 = mutableState3;
                } else {
                    object9 = object12;
                }
                mutableState = (MutableState)object9;
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
                MutableState dismissCommitted$delegate = mutableState;
                Unit unit = Unit.INSTANCE;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)1413809848, (String)"CC(remember):GroupActivity.kt#9igjgp");
                composer2 = $composer;
                boolean bl10 = false;
                boolean bl11 = false;
                Object object13 = composer.rememberedValue();
                boolean bl12 = false;
                if (object13 == Composer.Companion.getEmpty()) {
                    object8 = unit;
                    boolean bl13 = false;
                    unit = object8;
                    Function2 function2 = (Function2)new Function2<CoroutineScope, Continuation<? super Unit>, Object>((MutableState<Boolean>)visible$delegate, null){
                        int label;
                        final /* synthetic */ MutableState<Boolean> $visible$delegate;
                        {
                            this.$visible$delegate = $visible$delegate;
                            super(2, $completion);
                        }

                        public final Object invokeSuspend(Object $result) {
                            IntrinsicsKt.getCOROUTINE_SUSPENDED();
                            switch (this.label) {
                                case 0: {
                                    ResultKt.throwOnFailure((Object)$result);
                                    GroupActivity.access$GroupAddExistingMemorySheet$lambda$290(this.$visible$delegate, true);
                                    return Unit.INSTANCE;
                                }
                            }
                            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                        }

                        public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
                            return (Continuation)new /* invalid duplicate definition of identical inner class */;
                        }

                        public final Object invoke(CoroutineScope p1, Continuation<? super Unit> p2) {
                            return (this.create(p1, p2)).invokeSuspend(Unit.INSTANCE);
                        }
                    };
                    composer.updateRememberedValue((Object)function2);
                    object7 = function2;
                } else {
                    object7 = object13;
                }
                mutableState = (Function2)object7;
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
                EffectsKt.LaunchedEffect((Object)unit, (Function2)mutableState, (Composer)$composer, (int)6);
                Boolean bl14 = GroupActivity.GroupAddExistingMemorySheet$lambda$289((MutableState<Boolean>)visible$delegate);
                ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)1413812283, (String)"CC(remember):GroupActivity.kt#9igjgp");
                composer = $composer;
                boolean invalid\142 = ($dirty & 0x380000) == 0x100000;
                boolean bl15 = false;
                Object object14 = function04.rememberedValue();
                boolean bl16 = false;
                if (invalid\142 || object14 == Composer.Companion.getEmpty()) {
                    object8 = bl14;
                    boolean bl17 = false;
                    bl14 = object8;
                    Function2 function2 = (Function2)new Function2<CoroutineScope, Continuation<? super Unit>, Object>(onDismiss, (MutableState<Boolean>)visible$delegate, (MutableState<Boolean>)dismissCommitted$delegate, null){
                        int label;
                        final /* synthetic */ Function0<Unit> $onDismiss;
                        final /* synthetic */ MutableState<Boolean> $visible$delegate;
                        final /* synthetic */ MutableState<Boolean> $dismissCommitted$delegate;
                        {
                            this.$onDismiss = $onDismiss;
                            this.$visible$delegate = $visible$delegate;
                            this.$dismissCommitted$delegate = $dismissCommitted$delegate;
                            super(2, $completion);
                        }

                        /*
                         * Unable to fully structure code
                         */
                        public final Object invokeSuspend(Object $result) {
                            var2_2 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                            switch (this.label) {
                                case 0: {
                                    ResultKt.throwOnFailure((Object)$result);
                                    if (GroupActivity.access$GroupAddExistingMemorySheet$lambda$289(this.$visible$delegate) || GroupActivity.access$GroupAddExistingMemorySheet$lambda$292(this.$dismissCommitted$delegate)) ** GOTO lbl17
                                    GroupActivity.access$GroupAddExistingMemorySheet$lambda$293(this.$dismissCommitted$delegate, true);
                                    this.label = 1;
                                    v0 = DelayKt.delay((long)220L, (Continuation)((Continuation)this));
                                    if (v0 == var2_2) {
                                        return var2_2;
                                    }
                                    ** GOTO lbl15
                                }
                                case 1: {
                                    ResultKt.throwOnFailure((Object)$result);
                                    v0 = $result;
lbl15:
                                    // 2 sources

                                    this.$onDismiss.invoke();
lbl17:
                                    // 2 sources

                                    return Unit.INSTANCE;
                                }
                            }
                            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                        }

                        public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
                            return (Continuation)new /* invalid duplicate definition of identical inner class */;
                        }

                        public final Object invoke(CoroutineScope p1, Continuation<? super Unit> p2) {
                            return (this.create(p1, p2)).invokeSuspend(Unit.INSTANCE);
                        }
                    };
                    function04.updateRememberedValue((Object)function2);
                    object6 = function2;
                } else {
                    object6 = object14;
                }
                mutableState = (Function2)object6;
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
                EffectsKt.LaunchedEffect((Object)bl14, (Function2)mutableState, (Composer)$composer, (int)0);
                ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)1413818487, (String)"CC(remember):GroupActivity.kt#9igjgp");
                Composer invalid\142 = $composer;
                boolean invalid\172 = false;
                boolean bl18 = false;
                Object object15 = function03.rememberedValue();
                boolean bl19 = false;
                if (object15 == Composer.Companion.getEmpty()) {
                    boolean bl20 = false;
                    Function0 function05 = () -> GroupActivity.GroupAddExistingMemorySheet$lambda$297$lambda$296(visible$delegate);
                    function03.updateRememberedValue((Object)function05);
                    object5 = function05;
                } else {
                    object5 = object15;
                }
                function04 = (Function0)object5;
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
                Function0 tryDismiss = function04;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)1413822726, (String)"CC(remember):GroupActivity.kt#9igjgp");
                Composer invalid\172 = $composer;
                boolean invalid\202 = ($dirty & 0x1C00000) == 0x800000;
                boolean bl21 = false;
                Object object16 = function02.rememberedValue();
                boolean bl22 = false;
                if (invalid\202 || object16 == Composer.Companion.getEmpty()) {
                    boolean bl23 = false;
                    Function0 function06 = () -> GroupActivity.GroupAddExistingMemorySheet$lambda$299$lambda$298(onConfirm, visible$delegate);
                    function02.updateRememberedValue((Object)function06);
                    object4 = function06;
                } else {
                    object4 = object16;
                }
                function03 = (Function0)object4;
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
                Function0 requestConfirm = function03;
                NoMemoSheetDragController sheetDrag = ComposeUiKt.rememberNoMemoSheetDragController-hFKHopI((Function0<Boolean>)tryDismiss, 0.0f, 0.0f, 0.0f, $composer, 6, 14);
                boolean bl24 = GroupActivity.GroupAddExistingMemorySheet$lambda$289((MutableState<Boolean>)visible$delegate);
                ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)1413831510, (String)"CC(remember):GroupActivity.kt#9igjgp");
                Composer invalid\202 = $composer;
                boolean bl25 = false;
                boolean $i$f$cache\22\16992 = false;
                Object it\232 = $this$cache\22.rememberedValue();
                boolean bl26 = false;
                if (it\232 == Composer.Companion.getEmpty()) {
                    boolean bl27 = bl24;
                    boolean bl28 = false;
                    Function0 function07 = () -> GroupActivity.GroupAddExistingMemorySheet$lambda$301$lambda$300(tryDismiss);
                    bl24 = bl27;
                    function0 = function07;
                    $this$cache\22.updateRememberedValue((Object)function0);
                    object3 = function0;
                } else {
                    object3 = it\232;
                }
                function02 = (Function0)object3;
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
                BackHandlerKt.BackHandler((boolean)bl24, (Function0)function02, (Composer)$composer, (int)48, (int)0);
                function02 = ZIndexModifierKt.zIndex((Modifier)SizeKt.fillMaxSize$default((Modifier)((Modifier)Modifier.Companion), (float)0.0f, (int)1, null), (float)20.0f);
                Composer $i$f$cache\22\16992 = $composer;
                int it\232 = 6;
                boolean bl29 = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\25, (int)1042775818, (String)"CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo");
                Alignment alignment = Alignment.Companion.getTopStart();
                boolean bl30 = false;
                MeasurePolicy measurePolicy = BoxKt.maybeCachedBoxMeasurePolicy((Alignment)alignment, (boolean)bl30);
                function0 = modifier\25;
                int n4 = 0x70 & $changed\25 << 3;
                boolean bl31 = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\25, (int)-1159599143, (String)"CC(Layout)P(!1,2)80@3267L27,83@3433L360:Layout.kt#80mrfh");
                int n5 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode((Composer)$composer\25, (int)0));
                CompositionLocalMap compositionLocalMap = $composer\25.getCurrentCompositionLocalMap();
                Modifier modifier = ComposedModifierKt.materializeModifier((Composer)$composer\25, (Modifier)modifier\26);
                Function0 function08 = ComposeUiNode.Companion.getConstructor();
                int n6 = 6 | 0x380 & $changed\26 << 6;
                boolean bl32 = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\25, (int)-553112988, (String)"CC(ReusableComposeNode)N(factory,update,content)399@15590L9:Composables.kt#9igjgp");
                if (!($composer\25.getApplier() instanceof Applier)) {
                    ComposablesKt.invalidApplier();
                }
                $composer\25.startReusableNode();
                if ($composer\25.getInserting()) {
                    void factory\27;
                    $composer\25.createNode((Function0)factory\27);
                } else {
                    $composer\25.useNode();
                }
                Composer composer4 = Updater.constructor-impl((Composer)$composer\25);
                boolean bl33 = false;
                Updater.set-impl((Composer)composer4, (Object)measurePolicy, (Function2)ComposeUiNode.Companion.getSetMeasurePolicy());
                Updater.set-impl((Composer)composer4, (Object)compositionLocalMap, (Function2)ComposeUiNode.Companion.getSetResolvedCompositionLocals());
                Function2 function2 = ComposeUiNode.Companion.getSetCompositeKeyHash();
                boolean bl34 = false;
                Composer composer5 = composer4;
                boolean bl35 = false;
                if (composer5.getInserting() || !Intrinsics.areEqual((Object)composer5.rememberedValue(), (Object)n5)) {
                    composer5.updateRememberedValue((Object)n5);
                    composer4.apply((Object)n5, function2);
                }
                Updater.set-impl((Composer)composer4, (Object)modifier, (Function2)ComposeUiNode.Companion.getSetModifier());
                int n7 = 0xE & $changed\27 >> 6;
                void $composer\31 = $composer\25;
                boolean bl36 = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\31, (int)1833054614, (String)"C72@3469L9:Box.kt#2w3rfo");
                int n8 = 6 | 0x70 & $changed\25 >> 6;
                void var53_81 = $composer\31;
                BoxScope boxScope = (BoxScope)BoxScopeInstance.INSTANCE;
                boolean bl37 = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\32, (int)1380231321, (String)"C1710@80066L627,1706@79844L849,1730@80842L28,1734@81088L28,1739@81357L7765,1727@80707L8415:GroupActivity.kt#83vr7l");
                AnimatedVisibilityKt.AnimatedVisibility((boolean)GroupActivity.GroupAddExistingMemorySheet$lambda$289((MutableState<Boolean>)visible$delegate), null, (EnterTransition)EnterExitTransitionKt.fadeIn$default((FiniteAnimationSpec)((FiniteAnimationSpec)AnimationSpecKt.tween$default((int)180, (int)0, null, (int)6, null)), (float)0.0f, (int)2, null), (ExitTransition)EnterExitTransitionKt.fadeOut$default((FiniteAnimationSpec)((FiniteAnimationSpec)AnimationSpecKt.tween$default((int)180, (int)0, null, (int)6, null)), (float)0.0f, (int)2, null), null, (Function3)((Function3)ComposableLambdaKt.rememberComposableLambda((int)-855191084, (boolean)true, (arg_0, arg_1, arg_2) -> GroupActivity.GroupAddExistingMemorySheet$lambda$331$lambda$305(isDark, sheetDrag, tryDismiss, arg_0, arg_1, arg_2), (Composer)$composer\32, (int)54)), (Composer)$composer\32, (int)200064, (int)18);
                boolean bl38 = GroupActivity.GroupAddExistingMemorySheet$lambda$289((MutableState<Boolean>)visible$delegate);
                FiniteAnimationSpec finiteAnimationSpec2 = (FiniteAnimationSpec)AnimationSpecKt.tween$default((int)260, (int)0, null, (int)6, null);
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\32, (int)-925285048, (String)"CC(remember):GroupActivity.kt#9igjgp");
                Function1 function1 = $composer\32;
                boolean invalid\342 = false;
                boolean bl39 = false;
                Object object17 = $this$cache\33.rememberedValue();
                boolean bl40 = false;
                if (object17 == Composer.Companion.getEmpty()) {
                    finiteAnimationSpec = finiteAnimationSpec2;
                    boolean bl41 = false;
                    finiteAnimationSpec2 = finiteAnimationSpec;
                    Function1 function12 = GroupActivity::GroupAddExistingMemorySheet$lambda$331$lambda$307$lambda$306;
                    $this$cache\33.updateRememberedValue((Object)function12);
                    object2 = function12;
                } else {
                    object2 = object17;
                }
                Function1 function13 = (Function1)object2;
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\32);
                EnterTransition enterTransition = EnterExitTransitionKt.slideInVertically((FiniteAnimationSpec)finiteAnimationSpec2, (Function1)function13).plus(EnterExitTransitionKt.fadeIn$default((FiniteAnimationSpec)((FiniteAnimationSpec)AnimationSpecKt.tween$default((int)180, (int)0, null, (int)6, null)), (float)0.0f, (int)2, null));
                FiniteAnimationSpec finiteAnimationSpec3 = (FiniteAnimationSpec)AnimationSpecKt.tween$default((int)220, (int)0, null, (int)6, null);
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\32, (int)-925277176, (String)"CC(remember):GroupActivity.kt#9igjgp");
                void invalid\342 = $composer\32;
                boolean bl42 = false;
                boolean bl43 = false;
                Object object18 = $this$cache\36.rememberedValue();
                boolean bl44 = false;
                if (object18 == Composer.Companion.getEmpty()) {
                    finiteAnimationSpec = finiteAnimationSpec3;
                    boolean bl45 = false;
                    finiteAnimationSpec3 = finiteAnimationSpec;
                    Function1 function14 = GroupActivity::GroupAddExistingMemorySheet$lambda$331$lambda$309$lambda$308;
                    $this$cache\36.updateRememberedValue((Object)function14);
                    object = function14;
                } else {
                    object = object18;
                }
                function1 = (Function1)object;
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\32);
                ExitTransition exitTransition = EnterExitTransitionKt.slideOutVertically((FiniteAnimationSpec)finiteAnimationSpec3, (Function1)function1).plus(EnterExitTransitionKt.fadeOut$default((FiniteAnimationSpec)((FiniteAnimationSpec)AnimationSpecKt.tween$default((int)150, (int)0, null, (int)6, null)), (float)0.0f, (int)2, null));
                function13 = boxScope.align((Modifier)Modifier.Companion, Alignment.Companion.getBottomCenter());
                AnimatedVisibilityKt.AnimatedVisibility((boolean)bl38, (Modifier)function13, (EnterTransition)enterTransition, (ExitTransition)exitTransition, null, (Function3)((Function3)ComposableLambdaKt.rememberComposableLambda((int)-557691587, (boolean)true, (arg_0, arg_1, arg_2) -> GroupActivity.GroupAddExistingMemorySheet$lambda$331$lambda$330(sheetDrag, adaptive, panelSurface, bodyHeight, dragHandleColor, searchSurface, records, selectedRecordIds, onToggleRecord, palette, requestConfirm, tryDismiss, searchQuery, onSearchQueryChange, arg_0, arg_1, arg_2), (Composer)$composer\32, (int)54)), (Composer)$composer\32, (int)196608, (int)16);
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\32);
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\31);
                $composer\25.endNode();
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\25);
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\25);
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\25);
                if (ComposerKt.isTraceInProgress()) {
                    ComposerKt.traceEventEnd();
                }
            } else {
                $composer.skipToGroupEnd();
            }
            ScopeUpdateScope scopeUpdateScope = $composer.endRestartGroup();
            if (scopeUpdateScope == null) break block33;
            scopeUpdateScope.updateScope((arg_0, arg_1) -> GroupActivity.GroupAddExistingMemorySheet$lambda$332(this, $this$GroupAddExistingMemorySheet, records, selectedRecordIds, searchQuery, onSearchQueryChange, onToggleRecord, onDismiss, onConfirm, $changed, arg_0, arg_1));
        }
    }

    /*
     * WARNING - void declaration
     */
    @Composable
    @ComposableTarget(applier="androidx.compose.ui.UiComposable")
    private final void GroupEditAlbumSheet(BoxScope $this$GroupEditAlbumSheet, String title, String albumName, String albumDescription, boolean showOrganizeToggle, boolean autoClassifyEnabled, Function1<? super Boolean, Unit> onAutoClassifyEnabledChange, Function1<? super String, Unit> onNameChange, Function1<? super String, Unit> onDescriptionChange, Function0<Unit> onDismiss, Function0<Boolean> onConfirm, Composer $composer, int $changed, int $changed1) {
        block49: {
            $composer = $composer.startRestartGroup(1817011635);
            ComposerKt.sourceInformation((Composer)$composer, (String)"C(GroupEditAlbumSheet)N(title,albumName,albumDescription,showOrganizeToggle,autoClassifyEnabled,onAutoClassifyEnabledChange,onNameChange,onDescriptionChange,onDismiss,onConfirm)1913@89547L28,1914@89594L23,1915@89635L21,1916@89688L7,1917@89715L44,1930@90263L223,1937@90520L21,1938@90561L34,1939@90624L34,1940@90685L95,1943@90814L109,1947@90955L149,1947@90929L175,1952@91142L230,1952@91109L263,1961@91405L376,1961@91378L403,1972@91808L18,1972@91787L39,1973@91855L145,1973@91831L169,1981@92023L81,1987@92130L124,1994@92275L64,1996@92376L16,1996@92345L47,1998@92398L9771:GroupActivity.kt#83vr7l");
            int $dirty = $changed;
            int $dirty1 = $changed1;
            if (($changed & 0x30) == 0) {
                $dirty |= $composer.changed((Object)title) ? 32 : 16;
            }
            if (($changed & 0x180) == 0) {
                $dirty |= $composer.changed((Object)albumName) ? 256 : 128;
            }
            if (($changed & 0xC00) == 0) {
                $dirty |= $composer.changed((Object)albumDescription) ? 2048 : 1024;
            }
            if (($changed & 0x6000) == 0) {
                $dirty |= $composer.changed(showOrganizeToggle) ? 16384 : 8192;
            }
            if (($changed & 0x30000) == 0) {
                $dirty |= $composer.changed(autoClassifyEnabled) ? 131072 : 65536;
            }
            if (($changed & 0x180000) == 0) {
                $dirty |= $composer.changedInstance(onAutoClassifyEnabledChange) ? 0x100000 : 524288;
            }
            if (($changed & 0xC00000) == 0) {
                $dirty |= $composer.changedInstance(onNameChange) ? 0x800000 : 0x400000;
            }
            if (($changed & 0x6000000) == 0) {
                $dirty |= $composer.changedInstance(onDescriptionChange) ? 0x4000000 : 0x2000000;
            }
            if (($changed & 0x30000000) == 0) {
                $dirty |= $composer.changedInstance(onDismiss) ? 0x20000000 : 0x10000000;
            }
            if (($changed1 & 6) == 0) {
                $dirty1 |= $composer.changedInstance(onConfirm) ? 4 : 2;
            }
            if (($changed1 & 0x30) == 0) {
                $dirty1 |= $composer.changedInstance((Object)this) ? 32 : 16;
            }
            if ($composer.shouldExecute(($dirty & 0x12492491) != 306783376 || ($dirty1 & 0x13) != 18, $dirty & 1)) {
                Object object;
                void $this$cache\55;
                Object object2;
                FiniteAnimationSpec finiteAnimationSpec;
                void $this$cache\52;
                void $composer\51;
                void $changed\46;
                void $changed\45;
                void modifier\45;
                void $changed\44;
                void modifier\44;
                void $composer\44;
                Object object3;
                Function0 function0;
                void $this$cache\41;
                Object object4;
                Function0 function02;
                Object object5;
                Function0 function03;
                Object object6;
                Function0 function04;
                Object object7;
                Composer composer;
                Object object8;
                Composer composer2;
                Object object9;
                Composer composer3;
                Object object10;
                Object object11;
                Composer composer4;
                Object object12;
                Composer composer5;
                Object object13;
                MutableState mutableState;
                Object object14;
                MutableState mutableState2;
                Object object15;
                MutableState mutableState3;
                Object object16;
                void $this$cache\2;
                void this_\1;
                Activity activity;
                if (ComposerKt.isTraceInProgress()) {
                    ComposerKt.traceEventStart((int)1817011635, (int)$dirty, (int)$dirty1, (String)"com.han.nomemo.GroupActivity.GroupEditAlbumSheet (GroupActivity.kt:1912)");
                }
                NoMemoAdaptiveSpec adaptive = ComposeUiKt.rememberNoMemoAdaptiveSpec($composer, 0);
                NoMemoPalette palette = ComposeUiKt.rememberNoMemoPalette($composer, 0);
                boolean isDark = DarkThemeKt.isSystemInDarkTheme((Composer)$composer, (int)0);
                CompositionLocal compositionLocal = (CompositionLocal)AndroidCompositionLocals_androidKt.getLocalContext();
                Composer composer6 = $composer;
                boolean $changed\22 = false;
                boolean bl = false;
                ComposerKt.sourceInformationMarkerStart((Composer)activity, (int)2023513938, (String)"CC(<get-current>):CompositionLocal.kt#9igjgp");
                Object object17 = activity.consume((CompositionLocal)this_\1);
                ComposerKt.sourceInformationMarkerEnd((Composer)activity);
                Context context = (Context)object17;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)-1903489793, (String)"CC(remember):GroupActivity.kt#9igjgp");
                Composer $changed\22 = $composer;
                boolean bl2 = $composer.changed((Object)context);
                boolean bl3 = false;
                Object object18 = $this$cache\2.rememberedValue();
                boolean bl4 = false;
                if (bl2 || object18 == Composer.Companion.getEmpty()) {
                    boolean bl5 = false;
                    Activity activity2 = this.findActivity(context);
                    $this$cache\2.updateRememberedValue((Object)activity2);
                    object16 = activity2;
                } else {
                    object16 = object18;
                }
                activity = (Activity)object16;
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
                Activity activity3 = activity;
                long panelSurface = ComposeUiKt.noMemoThemeSyncedSheetSurface(palette, isDark);
                long inputSurface = ComposeUiKt.noMemoThemeSyncedContentSurface-t6yy7ic$default(palette, isDark, ComposeUiKt.noMemoCardSurfaceColor-4WTKRHQ(true, Color.copy-wmQWz5c$default((long)palette.getGlassFill-0d7_KjU(), (float)0.96f, (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null)), Color.copy-wmQWz5c$default((long)Color.Companion.getWhite-0d7_KjU(), (float)0.995f, (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null), 0.0f, 0.0f, 0.0f, 0.0f, 240, null);
                long dragHandleColor = isDark ? Color.copy-wmQWz5c$default((long)ColorKt.Color((long)4287532691L), (float)0.72f, (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null) : Color.copy-wmQWz5c$default((long)ColorKt.Color((long)4287532691L), (float)0.68f, (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null);
                int n = 450;
                boolean bl6 = false;
                int n2 = 500;
                boolean bl7 = false;
                int n3 = 300;
                boolean bl8 = false;
                float bodyHeight = ComposeUiKt.rememberNoMemoSheetHeight-kEYG_aY(Dp.constructor-impl((float)n), Dp.constructor-impl((float)n2), 0.76f, 0.7f, Dp.constructor-impl((float)n3), $composer, 28086, 0);
                ScrollState descriptionScrollState = ScrollKt.rememberScrollState((int)0, (Composer)$composer, (int)0, (int)1);
                ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)-1903462731, (String)"CC(remember):GroupActivity.kt#9igjgp");
                Composer composer7 = $composer;
                boolean invalid\92 = false;
                boolean bl9 = false;
                Object object19 = mutableState3.rememberedValue();
                boolean bl10 = false;
                if (object19 == Composer.Companion.getEmpty()) {
                    boolean bl11 = false;
                    MutableState mutableState4 = SnapshotStateKt.mutableStateOf$default((Object)false, null, (int)2, null);
                    mutableState3.updateRememberedValue((Object)mutableState4);
                    object15 = mutableState4;
                } else {
                    object15 = object19;
                }
                MutableState mutableState5 = (MutableState)object15;
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
                MutableState visible$delegate = mutableState5;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)-1903460715, (String)"CC(remember):GroupActivity.kt#9igjgp");
                Composer invalid\92 = $composer;
                boolean invalid\122 = false;
                boolean bl12 = false;
                Object object20 = mutableState2.rememberedValue();
                boolean bl13 = false;
                if (object20 == Composer.Companion.getEmpty()) {
                    boolean bl14 = false;
                    MutableState mutableState6 = SnapshotStateKt.mutableStateOf$default((Object)false, null, (int)2, null);
                    mutableState2.updateRememberedValue((Object)mutableState6);
                    object14 = mutableState6;
                } else {
                    object14 = object20;
                }
                mutableState3 = (MutableState)object14;
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
                MutableState dismissCommitted$delegate = mutableState3;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)-1903458702, (String)"CC(remember):GroupActivity.kt#9igjgp");
                Composer invalid\122 = $composer;
                boolean invalid\152 = false;
                boolean bl15 = false;
                Object object21 = mutableState.rememberedValue();
                boolean bl16 = false;
                if (object21 == Composer.Companion.getEmpty()) {
                    boolean bl17 = false;
                    MutableState mutableState7 = SnapshotStateKt.mutableStateOf$default((Object)new TextFieldValue(albumName, TextRangeKt.TextRange((int)albumName.length()), null, 4, null), null, (int)2, null);
                    mutableState.updateRememberedValue((Object)mutableState7);
                    object13 = mutableState7;
                } else {
                    object13 = object21;
                }
                mutableState2 = (MutableState)object13;
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
                MutableState albumNameField$delegate = mutableState2;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)-1903454560, (String)"CC(remember):GroupActivity.kt#9igjgp");
                Composer invalid\152 = $composer;
                boolean bl18 = false;
                boolean bl19 = false;
                Object object22 = composer5.rememberedValue();
                boolean bl20 = false;
                if (object22 == Composer.Companion.getEmpty()) {
                    boolean bl21 = false;
                    MutableState mutableState8 = SnapshotStateKt.mutableStateOf$default((Object)new TextFieldValue(albumDescription, TextRangeKt.TextRange((int)albumDescription.length()), null, 4, null), null, (int)2, null);
                    composer5.updateRememberedValue((Object)mutableState8);
                    object12 = mutableState8;
                } else {
                    object12 = object22;
                }
                mutableState = (MutableState)object12;
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
                MutableState albumDescriptionField$delegate = mutableState;
                String string2 = albumName;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)-1903450008, (String)"CC(remember):GroupActivity.kt#9igjgp");
                composer5 = $composer;
                boolean bl22 = ($dirty & 0x380) == 256;
                boolean bl23 = false;
                Object object23 = composer4.rememberedValue();
                boolean bl24 = false;
                if (bl22 || object23 == Composer.Companion.getEmpty()) {
                    object11 = string2;
                    boolean bl25 = false;
                    string2 = object11;
                    Function2 function2 = (Function2)new Function2<CoroutineScope, Continuation<? super Unit>, Object>(albumName, (MutableState<TextFieldValue>)albumNameField$delegate, null){
                        int label;
                        final /* synthetic */ String $albumName;
                        final /* synthetic */ MutableState<TextFieldValue> $albumNameField$delegate;
                        {
                            this.$albumName = $albumName;
                            this.$albumNameField$delegate = $albumNameField$delegate;
                            super(2, $completion);
                        }

                        public final Object invokeSuspend(Object $result) {
                            IntrinsicsKt.getCOROUTINE_SUSPENDED();
                            switch (this.label) {
                                case 0: {
                                    ResultKt.throwOnFailure((Object)$result);
                                    if (!Intrinsics.areEqual((Object)this.$albumName, (Object)GroupActivity.access$GroupEditAlbumSheet$lambda$341(this.$albumNameField$delegate).getText())) {
                                        GroupActivity.access$GroupEditAlbumSheet$lambda$342(this.$albumNameField$delegate, new TextFieldValue(this.$albumName, TextRangeKt.TextRange((int)this.$albumName.length()), null, 4, null));
                                    }
                                    return Unit.INSTANCE;
                                }
                            }
                            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                        }

                        public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
                            return (Continuation)new /* invalid duplicate definition of identical inner class */;
                        }

                        public final Object invoke(CoroutineScope p1, Continuation<? super Unit> p2) {
                            return (this.create(p1, p2)).invokeSuspend(Unit.INSTANCE);
                        }
                    };
                    composer4.updateRememberedValue((Object)function2);
                    object10 = function2;
                } else {
                    object10 = object23;
                }
                mutableState = (Function2)object10;
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
                EffectsKt.LaunchedEffect((Object)string2, (Function2)mutableState, (Composer)$composer, (int)(0xE & $dirty >> 6));
                String string3 = albumDescription;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)-1903443943, (String)"CC(remember):GroupActivity.kt#9igjgp");
                composer4 = $composer;
                boolean bl26 = ($dirty & 0x1C00) == 2048;
                boolean bl27 = false;
                Object object24 = composer3.rememberedValue();
                boolean bl28 = false;
                if (bl26 || object24 == Composer.Companion.getEmpty()) {
                    object11 = string3;
                    boolean bl29 = false;
                    string3 = object11;
                    Function2 function2 = (Function2)new Function2<CoroutineScope, Continuation<? super Unit>, Object>(albumDescription, (MutableState<TextFieldValue>)albumDescriptionField$delegate, null){
                        int label;
                        final /* synthetic */ String $albumDescription;
                        final /* synthetic */ MutableState<TextFieldValue> $albumDescriptionField$delegate;
                        {
                            this.$albumDescription = $albumDescription;
                            this.$albumDescriptionField$delegate = $albumDescriptionField$delegate;
                            super(2, $completion);
                        }

                        public final Object invokeSuspend(Object $result) {
                            IntrinsicsKt.getCOROUTINE_SUSPENDED();
                            switch (this.label) {
                                case 0: {
                                    ResultKt.throwOnFailure((Object)$result);
                                    if (!Intrinsics.areEqual((Object)this.$albumDescription, (Object)GroupActivity.access$GroupEditAlbumSheet$lambda$344(this.$albumDescriptionField$delegate).getText())) {
                                        GroupActivity.access$GroupEditAlbumSheet$lambda$345(this.$albumDescriptionField$delegate, new TextFieldValue(this.$albumDescription, TextRangeKt.TextRange((int)this.$albumDescription.length()), null, 4, null));
                                    }
                                    return Unit.INSTANCE;
                                }
                            }
                            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                        }

                        public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
                            return (Continuation)new /* invalid duplicate definition of identical inner class */;
                        }

                        public final Object invoke(CoroutineScope p1, Continuation<? super Unit> p2) {
                            return (this.create(p1, p2)).invokeSuspend(Unit.INSTANCE);
                        }
                    };
                    composer3.updateRememberedValue((Object)function2);
                    object9 = function2;
                } else {
                    object9 = object24;
                }
                mutableState = (Function2)object9;
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
                EffectsKt.LaunchedEffect((Object)string3, (Function2)mutableState, (Composer)$composer, (int)(0xE & $dirty >> 9));
                Object object25 = activity3;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)-1903435381, (String)"CC(remember):GroupActivity.kt#9igjgp");
                composer3 = $composer;
                boolean bl30 = $composer.changedInstance((Object)activity3);
                boolean bl31 = false;
                Object object26 = composer2.rememberedValue();
                boolean bl32 = false;
                if (bl30 || object26 == Composer.Companion.getEmpty()) {
                    object11 = object25;
                    boolean bl33 = false;
                    object25 = object11;
                    Function1 function1 = arg_0 -> GroupActivity.GroupEditAlbumSheet$lambda$350$lambda$349(activity3, arg_0);
                    composer2.updateRememberedValue((Object)function1);
                    object8 = function1;
                } else {
                    object8 = object26;
                }
                mutableState = (Function1)object8;
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
                EffectsKt.DisposableEffect((Object)object25, (Function1)mutableState, (Composer)$composer, (int)0);
                Object object27 = Unit.INSTANCE;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)-1903422843, (String)"CC(remember):GroupActivity.kt#9igjgp");
                composer2 = $composer;
                boolean bl34 = false;
                boolean bl35 = false;
                Object object28 = composer.rememberedValue();
                boolean bl36 = false;
                if (object28 == Composer.Companion.getEmpty()) {
                    object11 = object27;
                    boolean bl37 = false;
                    object27 = object11;
                    Function2 function2 = (Function2)new Function2<CoroutineScope, Continuation<? super Unit>, Object>((MutableState<Boolean>)visible$delegate, null){
                        int label;
                        final /* synthetic */ MutableState<Boolean> $visible$delegate;
                        {
                            this.$visible$delegate = $visible$delegate;
                            super(2, $completion);
                        }

                        public final Object invokeSuspend(Object $result) {
                            IntrinsicsKt.getCOROUTINE_SUSPENDED();
                            switch (this.label) {
                                case 0: {
                                    ResultKt.throwOnFailure((Object)$result);
                                    GroupActivity.access$GroupEditAlbumSheet$lambda$336(this.$visible$delegate, true);
                                    return Unit.INSTANCE;
                                }
                            }
                            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                        }

                        public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
                            return (Continuation)new /* invalid duplicate definition of identical inner class */;
                        }

                        public final Object invoke(CoroutineScope p1, Continuation<? super Unit> p2) {
                            return (this.create(p1, p2)).invokeSuspend(Unit.INSTANCE);
                        }
                    };
                    composer.updateRememberedValue((Object)function2);
                    object7 = function2;
                } else {
                    object7 = object28;
                }
                mutableState = (Function2)object7;
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
                EffectsKt.LaunchedEffect((Object)object27, (Function2)mutableState, (Composer)$composer, (int)6);
                Object object29 = GroupActivity.GroupEditAlbumSheet$lambda$335((MutableState<Boolean>)visible$delegate);
                ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)-1903421212, (String)"CC(remember):GroupActivity.kt#9igjgp");
                composer = $composer;
                boolean invalid\332 = ($dirty & 0x70000000) == 0x20000000;
                boolean bl38 = false;
                Object object30 = function04.rememberedValue();
                boolean bl39 = false;
                if (invalid\332 || object30 == Composer.Companion.getEmpty()) {
                    object11 = object29;
                    boolean bl40 = false;
                    object29 = object11;
                    Function2 function2 = (Function2)new Function2<CoroutineScope, Continuation<? super Unit>, Object>(onDismiss, (MutableState<Boolean>)visible$delegate, (MutableState<Boolean>)dismissCommitted$delegate, null){
                        int label;
                        final /* synthetic */ Function0<Unit> $onDismiss;
                        final /* synthetic */ MutableState<Boolean> $visible$delegate;
                        final /* synthetic */ MutableState<Boolean> $dismissCommitted$delegate;
                        {
                            this.$onDismiss = $onDismiss;
                            this.$visible$delegate = $visible$delegate;
                            this.$dismissCommitted$delegate = $dismissCommitted$delegate;
                            super(2, $completion);
                        }

                        /*
                         * Unable to fully structure code
                         */
                        public final Object invokeSuspend(Object $result) {
                            var2_2 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                            switch (this.label) {
                                case 0: {
                                    ResultKt.throwOnFailure((Object)$result);
                                    if (GroupActivity.access$GroupEditAlbumSheet$lambda$335(this.$visible$delegate) || GroupActivity.access$GroupEditAlbumSheet$lambda$338(this.$dismissCommitted$delegate)) ** GOTO lbl17
                                    GroupActivity.access$GroupEditAlbumSheet$lambda$339(this.$dismissCommitted$delegate, true);
                                    this.label = 1;
                                    v0 = DelayKt.delay((long)220L, (Continuation)((Continuation)this));
                                    if (v0 == var2_2) {
                                        return var2_2;
                                    }
                                    ** GOTO lbl15
                                }
                                case 1: {
                                    ResultKt.throwOnFailure((Object)$result);
                                    v0 = $result;
lbl15:
                                    // 2 sources

                                    this.$onDismiss.invoke();
lbl17:
                                    // 2 sources

                                    return Unit.INSTANCE;
                                }
                            }
                            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                        }

                        public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
                            return (Continuation)new /* invalid duplicate definition of identical inner class */;
                        }

                        public final Object invoke(CoroutineScope p1, Continuation<? super Unit> p2) {
                            return (this.create(p1, p2)).invokeSuspend(Unit.INSTANCE);
                        }
                    };
                    function04.updateRememberedValue((Object)function2);
                    object6 = function2;
                } else {
                    object6 = object30;
                }
                mutableState = (Function2)object6;
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
                EffectsKt.LaunchedEffect((Object)object29, (Function2)mutableState, (Composer)$composer, (int)0);
                ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)-1903415900, (String)"CC(remember):GroupActivity.kt#9igjgp");
                Composer invalid\332 = $composer;
                boolean invalid\362 = false;
                boolean bl41 = false;
                Object object31 = function03.rememberedValue();
                boolean bl42 = false;
                if (object31 == Composer.Companion.getEmpty()) {
                    boolean bl43 = false;
                    Function0 function05 = () -> GroupActivity.GroupEditAlbumSheet$lambda$354$lambda$353(visible$delegate);
                    function03.updateRememberedValue((Object)function05);
                    object5 = function05;
                } else {
                    object5 = object31;
                }
                function04 = (Function0)object5;
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
                Function0 tryDismiss = function04;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)-1903412433, (String)"CC(remember):GroupActivity.kt#9igjgp");
                Composer invalid\362 = $composer;
                boolean invalid\392 = ($dirty1 & 0xE) == 4;
                boolean bl44 = false;
                Object object32 = function02.rememberedValue();
                boolean bl45 = false;
                if (invalid\392 || object32 == Composer.Companion.getEmpty()) {
                    boolean bl46 = false;
                    Function0 function06 = () -> GroupActivity.GroupEditAlbumSheet$lambda$356$lambda$355(onConfirm, visible$delegate);
                    function02.updateRememberedValue((Object)function06);
                    object4 = function06;
                } else {
                    object4 = object32;
                }
                function03 = (Function0)object4;
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
                Function0 requestConfirm = function03;
                NoMemoSheetDragController sheetDrag = ComposeUiKt.rememberNoMemoSheetDragController-hFKHopI((Function0<Boolean>)tryDismiss, 0.0f, 0.0f, 0.0f, $composer, 6, 14);
                boolean bl47 = GroupActivity.GroupEditAlbumSheet$lambda$335((MutableState<Boolean>)visible$delegate);
                ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)-1903404669, (String)"CC(remember):GroupActivity.kt#9igjgp");
                Composer invalid\392 = $composer;
                boolean bl48 = false;
                boolean $i$f$cache\41\19982 = false;
                Object it\422 = $this$cache\41.rememberedValue();
                boolean bl49 = false;
                if (it\422 == Composer.Companion.getEmpty()) {
                    boolean bl50 = bl47;
                    boolean bl51 = false;
                    Function0 function07 = () -> GroupActivity.GroupEditAlbumSheet$lambda$358$lambda$357(tryDismiss);
                    bl47 = bl50;
                    function0 = function07;
                    $this$cache\41.updateRememberedValue((Object)function0);
                    object3 = function0;
                } else {
                    object3 = it\422;
                }
                function02 = (Function0)object3;
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
                BackHandlerKt.BackHandler((boolean)bl47, (Function0)function02, (Composer)$composer, (int)48, (int)0);
                function02 = ZIndexModifierKt.zIndex((Modifier)SizeKt.fillMaxSize$default((Modifier)((Modifier)Modifier.Companion), (float)0.0f, (int)1, null), (float)20.0f);
                Composer $i$f$cache\41\19982 = $composer;
                int it\422 = 6;
                boolean bl52 = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\44, (int)1042775818, (String)"CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo");
                Alignment alignment = Alignment.Companion.getTopStart();
                boolean bl53 = false;
                MeasurePolicy measurePolicy = BoxKt.maybeCachedBoxMeasurePolicy((Alignment)alignment, (boolean)bl53);
                function0 = modifier\44;
                int n4 = 0x70 & $changed\44 << 3;
                boolean bl54 = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\44, (int)-1159599143, (String)"CC(Layout)P(!1,2)80@3267L27,83@3433L360:Layout.kt#80mrfh");
                int n5 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode((Composer)$composer\44, (int)0));
                CompositionLocalMap compositionLocalMap = $composer\44.getCurrentCompositionLocalMap();
                Modifier modifier = ComposedModifierKt.materializeModifier((Composer)$composer\44, (Modifier)modifier\45);
                Function0 function08 = ComposeUiNode.Companion.getConstructor();
                int n6 = 6 | 0x380 & $changed\45 << 6;
                boolean bl55 = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\44, (int)-553112988, (String)"CC(ReusableComposeNode)N(factory,update,content)399@15590L9:Composables.kt#9igjgp");
                if (!($composer\44.getApplier() instanceof Applier)) {
                    ComposablesKt.invalidApplier();
                }
                $composer\44.startReusableNode();
                if ($composer\44.getInserting()) {
                    void factory\46;
                    $composer\44.createNode((Function0)factory\46);
                } else {
                    $composer\44.useNode();
                }
                Composer composer8 = Updater.constructor-impl((Composer)$composer\44);
                boolean bl56 = false;
                Updater.set-impl((Composer)composer8, (Object)measurePolicy, (Function2)ComposeUiNode.Companion.getSetMeasurePolicy());
                Updater.set-impl((Composer)composer8, (Object)compositionLocalMap, (Function2)ComposeUiNode.Companion.getSetResolvedCompositionLocals());
                Function2 function2 = ComposeUiNode.Companion.getSetCompositeKeyHash();
                boolean bl57 = false;
                Composer composer9 = composer8;
                boolean bl58 = false;
                if (composer9.getInserting() || !Intrinsics.areEqual((Object)composer9.rememberedValue(), (Object)n5)) {
                    composer9.updateRememberedValue((Object)n5);
                    composer8.apply((Object)n5, function2);
                }
                Updater.set-impl((Composer)composer8, (Object)modifier, (Function2)ComposeUiNode.Companion.getSetModifier());
                int n7 = 0xE & $changed\46 >> 6;
                void $composer\50 = $composer\44;
                boolean bl59 = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\50, (int)1833054614, (String)"C72@3469L9:Box.kt#2w3rfo");
                int n8 = 6 | 0x70 & $changed\44 >> 6;
                void var63_112 = $composer\50;
                BoxScope boxScope = (BoxScope)BoxScopeInstance.INSTANCE;
                boolean bl60 = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\51, (int)-521177174, (String)"C2007@92705L567,2003@92499L773,2027@93405L28,2031@93635L28,2035@93867L8296,2024@93282L8881:GroupActivity.kt#83vr7l");
                AnimatedVisibilityKt.AnimatedVisibility((boolean)GroupActivity.GroupEditAlbumSheet$lambda$335((MutableState<Boolean>)visible$delegate), null, (EnterTransition)EnterExitTransitionKt.fadeIn$default((FiniteAnimationSpec)((FiniteAnimationSpec)AnimationSpecKt.tween$default((int)180, (int)0, null, (int)6, null)), (float)0.0f, (int)2, null), (ExitTransition)EnterExitTransitionKt.fadeOut$default((FiniteAnimationSpec)((FiniteAnimationSpec)AnimationSpecKt.tween$default((int)180, (int)0, null, (int)6, null)), (float)0.0f, (int)2, null), null, (Function3)((Function3)ComposableLambdaKt.rememberComposableLambda((int)1311448465, (boolean)true, (arg_0, arg_1, arg_2) -> GroupActivity.GroupEditAlbumSheet$lambda$385$lambda$362(isDark, sheetDrag, tryDismiss, arg_0, arg_1, arg_2), (Composer)$composer\51, (int)54)), (Composer)$composer\51, (int)200064, (int)18);
                boolean bl61 = GroupActivity.GroupEditAlbumSheet$lambda$335((MutableState<Boolean>)visible$delegate);
                FiniteAnimationSpec finiteAnimationSpec2 = (FiniteAnimationSpec)AnimationSpecKt.tween$default((int)260, (int)0, null, (int)6, null);
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\51, (int)-16792811, (String)"CC(remember):GroupActivity.kt#9igjgp");
                Function1 function1 = $composer\51;
                boolean invalid\532 = false;
                boolean bl62 = false;
                Object object33 = $this$cache\52.rememberedValue();
                boolean bl63 = false;
                if (object33 == Composer.Companion.getEmpty()) {
                    finiteAnimationSpec = finiteAnimationSpec2;
                    boolean bl64 = false;
                    finiteAnimationSpec2 = finiteAnimationSpec;
                    Function1 function12 = GroupActivity::GroupEditAlbumSheet$lambda$385$lambda$364$lambda$363;
                    $this$cache\52.updateRememberedValue((Object)function12);
                    object2 = function12;
                } else {
                    object2 = object33;
                }
                Function1 function13 = (Function1)object2;
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\51);
                EnterTransition enterTransition = EnterExitTransitionKt.slideInVertically((FiniteAnimationSpec)finiteAnimationSpec2, (Function1)function13).plus(EnterExitTransitionKt.fadeIn$default((FiniteAnimationSpec)((FiniteAnimationSpec)AnimationSpecKt.tween$default((int)180, (int)0, null, (int)6, null)), (float)0.0f, (int)2, null));
                FiniteAnimationSpec finiteAnimationSpec3 = (FiniteAnimationSpec)AnimationSpecKt.tween$default((int)220, (int)0, null, (int)6, null);
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\51, (int)-16785451, (String)"CC(remember):GroupActivity.kt#9igjgp");
                void invalid\532 = $composer\51;
                boolean bl65 = false;
                boolean bl66 = false;
                Object object34 = $this$cache\55.rememberedValue();
                boolean bl67 = false;
                if (object34 == Composer.Companion.getEmpty()) {
                    finiteAnimationSpec = finiteAnimationSpec3;
                    boolean bl68 = false;
                    finiteAnimationSpec3 = finiteAnimationSpec;
                    Function1 function14 = GroupActivity::GroupEditAlbumSheet$lambda$385$lambda$366$lambda$365;
                    $this$cache\55.updateRememberedValue((Object)function14);
                    object = function14;
                } else {
                    object = object34;
                }
                function1 = (Function1)object;
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\51);
                ExitTransition exitTransition = EnterExitTransitionKt.slideOutVertically((FiniteAnimationSpec)finiteAnimationSpec3, (Function1)function1).plus(EnterExitTransitionKt.fadeOut$default((FiniteAnimationSpec)((FiniteAnimationSpec)AnimationSpecKt.tween$default((int)150, (int)0, null, (int)6, null)), (float)0.0f, (int)2, null));
                function13 = boxScope.align((Modifier)Modifier.Companion, Alignment.Companion.getBottomCenter());
                AnimatedVisibilityKt.AnimatedVisibility((boolean)bl61, (Modifier)function13, (EnterTransition)enterTransition, (ExitTransition)exitTransition, null, (Function3)((Function3)ComposableLambdaKt.rememberComposableLambda((int)-1772561400, (boolean)true, (arg_0, arg_1, arg_2) -> GroupActivity.GroupEditAlbumSheet$lambda$385$lambda$384(sheetDrag, adaptive, panelSurface, bodyHeight, dragHandleColor, requestConfirm, tryDismiss, title, palette, inputSurface, showOrganizeToggle, autoClassifyEnabled, onAutoClassifyEnabledChange, onNameChange, albumNameField$delegate, onDescriptionChange, albumDescriptionField$delegate, descriptionScrollState, arg_0, arg_1, arg_2), (Composer)$composer\51, (int)54)), (Composer)$composer\51, (int)196608, (int)16);
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\51);
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\50);
                $composer\44.endNode();
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\44);
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\44);
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\44);
                if (ComposerKt.isTraceInProgress()) {
                    ComposerKt.traceEventEnd();
                }
            } else {
                $composer.skipToGroupEnd();
            }
            ScopeUpdateScope scopeUpdateScope = $composer.endRestartGroup();
            if (scopeUpdateScope == null) break block49;
            scopeUpdateScope.updateScope((arg_0, arg_1) -> GroupActivity.GroupEditAlbumSheet$lambda$386(this, $this$GroupEditAlbumSheet, title, albumName, albumDescription, showOrganizeToggle, autoClassifyEnabled, onAutoClassifyEnabledChange, onNameChange, onDescriptionChange, onDismiss, onConfirm, $changed, $changed1, arg_0, arg_1));
        }
    }

    private final Activity findActivity(Context $this$findActivity) {
        Activity activity;
        block2: {
            GroupActivity groupActivity = this;
            while (true) {
                Context context;
                Context context2;
                if ((context2 = $this$findActivity) instanceof Activity) {
                    activity = (Activity)$this$findActivity;
                    break block2;
                }
                if (!(context2 instanceof ContextWrapper)) break;
                GroupActivity groupActivity2 = groupActivity;
                Intrinsics.checkNotNullExpressionValue((Object)((ContextWrapper)$this$findActivity).getBaseContext(), (String)"getBaseContext(...)");
                groupActivity = groupActivity2;
                $this$findActivity = context;
            }
            activity = null;
        }
        return activity;
    }

    @Composable
    @ComposableTarget(applier="androidx.compose.ui.UiComposable")
    private final void GroupAlbumInputField-hGBTI10(String value, Function1<? super String, Unit> onValueChange, String placeholder, float minHeight, Modifier modifier, Composer $composer, int $changed, int n) {
        block15: {
            $composer = $composer.startRestartGroup(1278001461);
            ComposerKt.sourceInformation((Composer)$composer, (String)"C(GroupAlbumInputField)N(value,onValueChange,placeholder,minHeight:c#ui.unit.Dp,modifier)2225@102638L23,2226@102683L21,2230@102850L203,2240@103231L1020,2227@102713L1538:GroupActivity.kt#83vr7l");
            int $dirty = $changed;
            if (($changed & 6) == 0) {
                $dirty |= $composer.changed((Object)value) ? 4 : 2;
            }
            if (($changed & 0x30) == 0) {
                $dirty |= $composer.changedInstance(onValueChange) ? 32 : 16;
            }
            if (($changed & 0x180) == 0) {
                $dirty |= $composer.changed((Object)placeholder) ? 256 : 128;
            }
            if ((n & 8) != 0) {
                $dirty |= 0xC00;
            } else if (($changed & 0xC00) == 0) {
                $dirty |= $composer.changed(minHeight) ? 2048 : 1024;
            }
            if ((n & 0x10) != 0) {
                $dirty |= 0x6000;
            } else if (($changed & 0x6000) == 0) {
                $dirty |= $composer.changed((Object)modifier) ? 16384 : 8192;
            }
            if ($composer.shouldExecute(($dirty & 0x2493) != 9362, $dirty & 1)) {
                if ((n & 8) != 0) {
                    int n2 = 46;
                    boolean bl = false;
                    minHeight = Dp.constructor-impl((float)n2);
                }
                if ((n & 0x10) != 0) {
                    modifier = (Modifier)Modifier.Companion;
                }
                if (ComposerKt.isTraceInProgress()) {
                    ComposerKt.traceEventStart((int)1278001461, (int)$dirty, (int)-1, (String)"com.han.nomemo.GroupActivity.GroupAlbumInputField (GroupActivity.kt:2224)");
                }
                NoMemoPalette palette = ComposeUiKt.rememberNoMemoPalette($composer, 0);
                boolean isDark = DarkThemeKt.isSystemInDarkTheme((Composer)$composer, (int)0);
                int n3 = 16;
                boolean bl = false;
                boolean bl2 = true;
                boolean bl3 = false;
                CardKt.Card((Modifier)SizeKt.fillMaxWidth$default((Modifier)modifier, (float)0.0f, (int)1, null), (Shape)((Shape)ComposeUiKt.noMemoG2RoundedShape-0680j_4(Dp.constructor-impl((float)n3))), (CardColors)CardDefaults.INSTANCE.cardColors-ro_MJ88(ComposeUiKt.noMemoCardSurfaceColor-4WTKRHQ(isDark, Color.copy-wmQWz5c$default((long)Color.Companion.getWhite-0d7_KjU(), (float)0.995f, (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null)), 0L, 0L, 0L, $composer, CardDefaults.$stable << 12, 14), null, (BorderStroke)BorderStrokeKt.BorderStroke-cXLIe8U((float)Dp.constructor-impl((float)((float)bl2)), (long)Color.copy-wmQWz5c$default((long)palette.getGlassStroke-0d7_KjU(), (float)(isDark ? 0.44f : 0.18f), (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null)), (Function3)((Function3)ComposableLambdaKt.rememberComposableLambda((int)-951448317, (boolean)true, (arg_0, arg_1, arg_2) -> GroupActivity.GroupAlbumInputField_hGBTI10$lambda$389(palette, minHeight, value, onValueChange, placeholder, arg_0, arg_1, arg_2), (Composer)$composer, (int)54)), (Composer)$composer, (int)196608, (int)8);
                if (ComposerKt.isTraceInProgress()) {
                    ComposerKt.traceEventEnd();
                }
            } else {
                $composer.skipToGroupEnd();
            }
            ScopeUpdateScope scopeUpdateScope = $composer.endRestartGroup();
            if (scopeUpdateScope == null) break block15;
            scopeUpdateScope.updateScope((arg_0, arg_1) -> GroupActivity.GroupAlbumInputField_hGBTI10$lambda$390(this, value, onValueChange, placeholder, minHeight, modifier, $changed, n, arg_0, arg_1));
        }
    }

    /*
     * WARNING - void declaration
     */
    @Composable
    @ComposableTarget(applier="androidx.compose.ui.UiComposable")
    private final void GroupAlbumDetailEmptyState(boolean organizeProcessing, Function0<Unit> onAddMemoryClick, Function0<Unit> onOrganizeClick, Composer $composer, int $changed) {
        block12: {
            $composer = $composer.startRestartGroup(-1235654192);
            ComposerKt.sourceInformation((Composer)$composer, (String)"C(GroupAlbumDetailEmptyState)N(organizeProcessing,onAddMemoryClick,onOrganizeClick)2277@104460L23,2278@104505L21,2280@104620L2108:GroupActivity.kt#83vr7l");
            int $dirty = $changed;
            if (($changed & 6) == 0) {
                $dirty |= $composer.changed(organizeProcessing) ? 4 : 2;
            }
            if (($changed & 0x30) == 0) {
                $dirty |= $composer.changedInstance(onAddMemoryClick) ? 32 : 16;
            }
            if (($changed & 0x180) == 0) {
                $dirty |= $composer.changedInstance(onOrganizeClick) ? 256 : 128;
            }
            if (($changed & 0xC00) == 0) {
                $dirty |= $composer.changedInstance((Object)this) ? 2048 : 1024;
            }
            if ($composer.shouldExecute(($dirty & 0x493) != 1170, $dirty & 1)) {
                void $composer\10;
                void $changed\5;
                void $changed\4;
                void modifier\4;
                void modifier\3;
                void $changed\3;
                void horizontalAlignment\3;
                void verticalArrangement\3;
                void $composer\3;
                if (ComposerKt.isTraceInProgress()) {
                    ComposerKt.traceEventStart((int)-1235654192, (int)$dirty, (int)-1, (String)"com.han.nomemo.GroupActivity.GroupAlbumDetailEmptyState (GroupActivity.kt:2276)");
                }
                NoMemoPalette palette = ComposeUiKt.rememberNoMemoPalette($composer, 0);
                boolean isDark = DarkThemeKt.isSystemInDarkTheme((Composer)$composer, (int)0);
                long primaryActionBlue = isDark ? ColorKt.Color((long)4283080191L) : ColorKt.Color((long)4279662591L);
                int n = -26;
                boolean bl = false;
                int n2 = 30;
                boolean bl2 = false;
                Modifier modifier = PaddingKt.padding-VpY3zN4$default((Modifier)OffsetKt.offset-VpY3zN4$default((Modifier)SizeKt.fillMaxWidth$default((Modifier)((Modifier)Modifier.Companion), (float)0.0f, (int)1, null), (float)0.0f, (float)Dp.constructor-impl((float)n), (int)1, null), (float)Dp.constructor-impl((float)n2), (float)0.0f, (int)2, null);
                Alignment.Horizontal horizontal = Alignment.Companion.getCenterHorizontally();
                Arrangement.HorizontalOrVertical horizontalOrVertical = Arrangement.INSTANCE.getCenter();
                Modifier modifier2 = modifier;
                Arrangement.Vertical vertical = (Arrangement.Vertical)horizontalOrVertical;
                Alignment.Horizontal horizontal2 = horizontal;
                Composer composer = $composer;
                int n3 = 438;
                boolean bl3 = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\3, (int)1341605231, (String)"CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo");
                MeasurePolicy measurePolicy = ColumnKt.columnMeasurePolicy((Arrangement.Vertical)verticalArrangement\3, (Alignment.Horizontal)horizontalAlignment\3, (Composer)$composer\3, (int)(0xE & $changed\3 >> 3 | 0x70 & $changed\3 >> 3));
                void var21_22 = modifier\3;
                int n4 = 0x70 & $changed\3 << 3;
                boolean bl4 = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\3, (int)-1159599143, (String)"CC(Layout)P(!1,2)80@3267L27,83@3433L360:Layout.kt#80mrfh");
                int n5 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode((Composer)$composer\3, (int)0));
                CompositionLocalMap compositionLocalMap = $composer\3.getCurrentCompositionLocalMap();
                Modifier modifier3 = ComposedModifierKt.materializeModifier((Composer)$composer\3, (Modifier)modifier\4);
                Function0 function0 = ComposeUiNode.Companion.getConstructor();
                int n6 = 6 | 0x380 & $changed\4 << 6;
                boolean bl5 = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\3, (int)-553112988, (String)"CC(ReusableComposeNode)N(factory,update,content)399@15590L9:Composables.kt#9igjgp");
                if (!($composer\3.getApplier() instanceof Applier)) {
                    ComposablesKt.invalidApplier();
                }
                $composer\3.startReusableNode();
                if ($composer\3.getInserting()) {
                    void factory\5;
                    $composer\3.createNode((Function0)factory\5);
                } else {
                    $composer\3.useNode();
                }
                Composer composer2 = Updater.constructor-impl((Composer)$composer\3);
                boolean bl6 = false;
                Updater.set-impl((Composer)composer2, (Object)measurePolicy, (Function2)ComposeUiNode.Companion.getSetMeasurePolicy());
                Updater.set-impl((Composer)composer2, (Object)compositionLocalMap, (Function2)ComposeUiNode.Companion.getSetResolvedCompositionLocals());
                Function2 function2 = ComposeUiNode.Companion.getSetCompositeKeyHash();
                boolean bl7 = false;
                Composer composer3 = composer2;
                boolean bl8 = false;
                if (composer3.getInserting() || !Intrinsics.areEqual((Object)composer3.rememberedValue(), (Object)n5)) {
                    composer3.updateRememberedValue((Object)n5);
                    composer2.apply((Object)n5, function2);
                }
                Updater.set-impl((Composer)composer2, (Object)modifier3, (Function2)ComposeUiNode.Companion.getSetModifier());
                int n7 = 0xE & $changed\5 >> 6;
                void $composer\9 = $composer\3;
                boolean bl9 = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\9, (int)2093002350, (String)"C89@4557L9:Column.kt#2w3rfo");
                int n8 = 6 | 0x70 & $changed\3 >> 6;
                void var40_41 = $composer\9;
                ColumnScope columnScope = (ColumnScope)ColumnScopeInstance.INSTANCE;
                boolean bl10 = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\10, (int)118642048, (String)"C2289@104949L44,2288@104917L247,2294@105177L41,2295@105231L174,2301@105418L41,2302@105472L181,2308@105666L41,2309@105720L212,2315@105945L41,2316@105999L719:GroupActivity.kt#83vr7l");
                Painter painter = PainterResources_androidKt.painterResource((int)R.drawable.ic_nm_group_dock, (Composer)$composer\10, (int)0);
                long l = Color.copy-wmQWz5c$default((long)palette.getTextTertiary-0d7_KjU(), (float)0.72f, (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null);
                int n9 = 66;
                boolean bl11 = false;
                Modifier modifier4 = SizeKt.size-3ABfNKs((Modifier)((Modifier)Modifier.Companion), (float)Dp.constructor-impl((float)n9));
                IconKt.Icon-ww6aTOc((Painter)painter, null, (Modifier)modifier4, (long)l, (Composer)$composer\10, (int)432, (int)0);
                int n10 = 18;
                boolean bl12 = false;
                SpacerKt.Spacer((Modifier)SizeKt.height-3ABfNKs((Modifier)((Modifier)Modifier.Companion), (float)Dp.constructor-impl((float)n10)), (Composer)$composer\10, (int)6);
                TextKt.Text--4IGK_g((String)"\u6682\u65e0\u8bb0\u5fc6", null, (long)palette.getTextPrimary-0d7_KjU(), (long)TextUnitKt.getSp((int)24), null, (FontWeight)FontWeight.Companion.getBold(), null, (long)0L, null, null, (long)0L, (int)0, (boolean)false, (int)0, (int)0, null, null, (Composer)$composer\10, (int)199686, (int)0, (int)131026);
                int n11 = 10;
                boolean bl13 = false;
                SpacerKt.Spacer((Modifier)SizeKt.height-3ABfNKs((Modifier)((Modifier)Modifier.Companion), (float)Dp.constructor-impl((float)n11)), (Composer)$composer\10, (int)6);
                TextKt.Text--4IGK_g((String)"\u8be5\u5206\u7ec4\u4e0b\u6682\u65e0\u8bb0\u5fc6", null, (long)palette.getTextTertiary-0d7_KjU(), (long)TextUnitKt.getSp((int)16), null, (FontWeight)FontWeight.Companion.getMedium(), null, (long)0L, null, null, (long)0L, (int)0, (boolean)false, (int)0, (int)0, null, null, (Composer)$composer\10, (int)199686, (int)0, (int)131026);
                int n12 = 34;
                boolean bl14 = false;
                SpacerKt.Spacer((Modifier)SizeKt.height-3ABfNKs((Modifier)((Modifier)Modifier.Companion), (float)Dp.constructor-impl((float)n12)), (Composer)$composer\10, (int)6);
                this.GroupAlbumEmptyActionButton-DTcfvLk("\u6dfb\u52a0\u8bb0\u5fc6", primaryActionBlue, Color.Companion.getWhite-0d7_KjU(), false, onAddMemoryClick, (Composer)$composer\10, 0x186 | 0xE000 & $dirty << 9 | 0x70000 & $dirty << 6, 8);
                int n13 = 14;
                boolean bl15 = false;
                SpacerKt.Spacer((Modifier)SizeKt.height-3ABfNKs((Modifier)((Modifier)Modifier.Companion), (float)Dp.constructor-impl((float)n13)), (Composer)$composer\10, (int)6);
                this.GroupAlbumEmptyActionButton-DTcfvLk(organizeProcessing ? "\u6574\u7406\u4e2d" : "\u6574\u7406\u5386\u53f2\u8bb0\u5fc6", ComposeUiKt.noMemoThemeSyncedContentSurface-t6yy7ic(palette, isDark, ComposeUiKt.noMemoCardSurfaceColor-4WTKRHQ(true, ColorKt.Color((long)4279704096L)), Color.copy-wmQWz5c$default((long)Color.Companion.getWhite-0d7_KjU(), (float)0.995f, (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null), 0.05f, 0.14f, 0.96f, 0.995f), palette.getTextPrimary-0d7_KjU(), !organizeProcessing, onOrganizeClick, (Composer)$composer\10, 0xE000 & $dirty << 6 | 0x70000 & $dirty << 6, 0);
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\10);
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\9);
                $composer\3.endNode();
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\3);
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\3);
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\3);
                if (ComposerKt.isTraceInProgress()) {
                    ComposerKt.traceEventEnd();
                }
            } else {
                $composer.skipToGroupEnd();
            }
            ScopeUpdateScope scopeUpdateScope = $composer.endRestartGroup();
            if (scopeUpdateScope == null) break block12;
            scopeUpdateScope.updateScope((arg_0, arg_1) -> GroupActivity.GroupAlbumDetailEmptyState$lambda$392(this, organizeProcessing, onAddMemoryClick, onOrganizeClick, $changed, arg_0, arg_1));
        }
    }

    /*
     * WARNING - void declaration
     */
    @Composable
    @ComposableTarget(applier="androidx.compose.ui.UiComposable")
    private final void GroupAlbumEmptyActionButton-DTcfvLk(String text, long containerColor, long contentColor, boolean enabled, Function0<Unit> onClick, Composer $composer, int $changed, int n) {
        block24: {
            $composer = $composer.startRestartGroup(-464241091);
            ComposerKt.sourceInformation((Composer)$composer, (String)"C(GroupAlbumEmptyActionButton)N(text,containerColor:c#ui.graphics.Color,contentColor:c#ui.graphics.Color,enabled,onClick)2343@106969L21,2344@107012L189,2349@107228L39,2350@107303L25,2357@107621L698:GroupActivity.kt#83vr7l");
            int $dirty = $changed;
            if (($changed & 6) == 0) {
                $dirty |= $composer.changed((Object)text) ? 4 : 2;
            }
            if (($changed & 0x30) == 0) {
                $dirty |= $composer.changed(containerColor) ? 32 : 16;
            }
            if (($changed & 0x180) == 0) {
                $dirty |= $composer.changed(contentColor) ? 256 : 128;
            }
            if ((n & 8) != 0) {
                $dirty |= 0xC00;
            } else if (($changed & 0xC00) == 0) {
                $dirty |= $composer.changed(enabled) ? 2048 : 1024;
            }
            if (($changed & 0x6000) == 0) {
                $dirty |= $composer.changedInstance(onClick) ? 16384 : 8192;
            }
            if ($composer.shouldExecute(($dirty & 0x2493) != 9362, $dirty & 1)) {
                Object object;
                void $this$cache\1;
                if ((n & 8) != 0) {
                    enabled = true;
                }
                if (ComposerKt.isTraceInProgress()) {
                    ComposerKt.traceEventStart((int)-464241091, (int)$dirty, (int)-1, (String)"com.han.nomemo.GroupActivity.GroupAlbumEmptyActionButton (GroupActivity.kt:2342)");
                }
                boolean isDark = DarkThemeKt.isSystemInDarkTheme((Composer)$composer, (int)0);
                State alpha$delegate = AnimateAsStateKt.animateFloatAsState((float)(enabled ? 1.0f : 0.7f), (AnimationSpec)((AnimationSpec)AnimationSpecKt.tween$default((int)180, (int)0, null, (int)6, null)), (float)0.0f, (String)"groupAlbumEmptyActionAlpha", null, (Composer)$composer, (int)3120, (int)20);
                ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)540925988, (String)"CC(remember):GroupActivity.kt#9igjgp");
                Composer composer = $composer;
                boolean bl = false;
                boolean bl2 = false;
                Object object2 = $this$cache\1.rememberedValue();
                boolean bl32 = false;
                if (object2 == Composer.Companion.getEmpty()) {
                    boolean bl4 = false;
                    MutableInteractionSource mutableInteractionSource = InteractionSourceKt.MutableInteractionSource();
                    $this$cache\1.updateRememberedValue((Object)mutableInteractionSource);
                    object = mutableInteractionSource;
                } else {
                    object = object2;
                }
                MutableInteractionSource mutableInteractionSource = (MutableInteractionSource)object;
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
                MutableInteractionSource interaction = mutableInteractionSource;
                State pressed$delegate = PressInteractionKt.collectIsPressedAsState((InteractionSource)((InteractionSource)interaction), (Composer)$composer, (int)6);
                int n2 = 26;
                boolean bl5 = false;
                ContinuousRoundedRectangle shape = ComposeUiKt.noMemoG2RoundedShape-0680j_4(Dp.constructor-impl((float)n2));
                long effectiveContainerColor = enabled && GroupActivity.GroupAlbumEmptyActionButton_DTcfvLk$lambda$395((State<Boolean>)pressed$delegate) ? (isDark ? ColorKt.Color((long)4281019956L) : ColorKt.Color((long)4293388782L)) : containerColor;
                Function3 buttonContent = (Function3)ComposableLambdaKt.rememberComposableLambda((int)-1724901705, (boolean)true, (arg_0, arg_1, arg_2) -> GroupActivity.GroupAlbumEmptyActionButton_DTcfvLk$lambda$398(shape, effectiveContainerColor, text, contentColor, arg_0, arg_1, arg_2), (Composer)$composer, (int)54);
                if (enabled) {
                    void n10;
                    void n8;
                    void modifier\6;
                    void bl14;
                    void modifier;
                    void composer5;
                    $composer.startReplaceGroup(-410048577);
                    ComposerKt.sourceInformation((Composer)$composer, (String)"2379@108355L366");
                    Modifier bl32 = ClickableKt.clickable-O2vRcR0$default((Modifier)AlphaKt.alpha((Modifier)SizeKt.fillMaxWidth((Modifier)((Modifier)Modifier.Companion), (float)0.62f), (float)GroupActivity.GroupAlbumEmptyActionButton_DTcfvLk$lambda$393((State<Float>)alpha$delegate)), (MutableInteractionSource)interaction, null, (boolean)false, null, null, onClick, (int)28, null);
                    Composer composer2 = $composer;
                    int n3 = 3072;
                    boolean bl6 = false;
                    ComposerKt.sourceInformationMarkerStart((Composer)composer5, (int)1042775818, (String)"CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo");
                    Alignment alignment = Alignment.Companion.getTopStart();
                    boolean bl7 = false;
                    MeasurePolicy measurePolicy = BoxKt.maybeCachedBoxMeasurePolicy((Alignment)alignment, (boolean)bl7);
                    void var27_34 = modifier;
                    int n4 = 0x70 & bl14 << 3;
                    boolean bl8 = false;
                    ComposerKt.sourceInformationMarkerStart((Composer)composer5, (int)-1159599143, (String)"CC(Layout)P(!1,2)80@3267L27,83@3433L360:Layout.kt#80mrfh");
                    int n5 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode((Composer)composer5, (int)0));
                    CompositionLocalMap compositionLocalMap = composer5.getCurrentCompositionLocalMap();
                    Modifier modifier = ComposedModifierKt.materializeModifier((Composer)composer5, (Modifier)modifier\6);
                    Function0 function0 = ComposeUiNode.Companion.getConstructor();
                    int n6 = 6 | 0x380 & n8 << 6;
                    boolean bl9 = false;
                    ComposerKt.sourceInformationMarkerStart((Composer)composer5, (int)-553112988, (String)"CC(ReusableComposeNode)N(factory,update,content)399@15590L9:Composables.kt#9igjgp");
                    if (!(composer5.getApplier() instanceof Applier)) {
                        ComposablesKt.invalidApplier();
                    }
                    composer5.startReusableNode();
                    if (composer5.getInserting()) {
                        void function0;
                        composer5.createNode((Function0)function0);
                    } else {
                        composer5.useNode();
                    }
                    Composer composer3 = Updater.constructor-impl((Composer)composer5);
                    boolean bl10 = false;
                    Updater.set-impl((Composer)composer3, (Object)measurePolicy, (Function2)ComposeUiNode.Companion.getSetMeasurePolicy());
                    Updater.set-impl((Composer)composer3, (Object)compositionLocalMap, (Function2)ComposeUiNode.Companion.getSetResolvedCompositionLocals());
                    Function2 function2 = ComposeUiNode.Companion.getSetCompositeKeyHash();
                    boolean bl11 = false;
                    Composer composer4 = composer3;
                    boolean bl12 = false;
                    if (composer4.getInserting() || !Intrinsics.areEqual((Object)composer4.rememberedValue(), (Object)n5)) {
                        composer4.updateRememberedValue((Object)n5);
                        composer3.apply((Object)n5, function2);
                    }
                    Updater.set-impl((Composer)composer3, (Object)modifier, (Function2)ComposeUiNode.Companion.getSetModifier());
                    int n7 = 0xE & n10 >> 6;
                    void $composer\11 = composer5;
                    boolean bl13 = false;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer\11, (int)1833054614, (String)"C72@3469L9:Box.kt#2w3rfo");
                    buttonContent.invoke((Object)BoxScopeInstance.INSTANCE, (Object)$composer\11, (Object)(6 | 0x70 & bl14 >> 6));
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer\11);
                    composer5.endNode();
                    ComposerKt.sourceInformationMarkerEnd((Composer)composer5);
                    ComposerKt.sourceInformationMarkerEnd((Composer)composer5);
                    ComposerKt.sourceInformationMarkerEnd((Composer)composer5);
                    $composer.endReplaceGroup();
                } else {
                    void $changed\19;
                    void $composer\19;
                    void $changed\14;
                    void $changed\13;
                    void modifier\13;
                    void $changed\12;
                    void modifier\12;
                    void $composer\12;
                    $composer.startReplaceGroup(-409661604);
                    ComposerKt.sourceInformation((Composer)$composer, (String)"2391@108751L177");
                    Modifier modifier = AlphaKt.alpha((Modifier)SizeKt.fillMaxWidth((Modifier)((Modifier)Modifier.Companion), (float)0.62f), (float)GroupActivity.GroupAlbumEmptyActionButton_DTcfvLk$lambda$393((State<Float>)alpha$delegate));
                    Composer composer5 = $composer;
                    boolean bl14 = false;
                    boolean bl15 = false;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer\12, (int)1042775818, (String)"CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo");
                    Alignment alignment = Alignment.Companion.getTopStart();
                    boolean bl16 = false;
                    MeasurePolicy measurePolicy = BoxKt.maybeCachedBoxMeasurePolicy((Alignment)alignment, (boolean)bl16);
                    void modifier\6 = modifier\12;
                    int n8 = 0x70 & $changed\12 << 3;
                    boolean bl17 = false;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer\12, (int)-1159599143, (String)"CC(Layout)P(!1,2)80@3267L27,83@3433L360:Layout.kt#80mrfh");
                    int n9 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode((Composer)$composer\12, (int)0));
                    CompositionLocalMap compositionLocalMap = $composer\12.getCurrentCompositionLocalMap();
                    Modifier modifier2 = ComposedModifierKt.materializeModifier((Composer)$composer\12, (Modifier)modifier\13);
                    Function0 function0 = ComposeUiNode.Companion.getConstructor();
                    int n10 = 6 | 0x380 & $changed\13 << 6;
                    boolean bl18 = false;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer\12, (int)-553112988, (String)"CC(ReusableComposeNode)N(factory,update,content)399@15590L9:Composables.kt#9igjgp");
                    if (!($composer\12.getApplier() instanceof Applier)) {
                        ComposablesKt.invalidApplier();
                    }
                    $composer\12.startReusableNode();
                    if ($composer\12.getInserting()) {
                        void factory\14;
                        $composer\12.createNode((Function0)factory\14);
                    } else {
                        $composer\12.useNode();
                    }
                    Composer composer6 = Updater.constructor-impl((Composer)$composer\12);
                    boolean bl19 = false;
                    Updater.set-impl((Composer)composer6, (Object)measurePolicy, (Function2)ComposeUiNode.Companion.getSetMeasurePolicy());
                    Updater.set-impl((Composer)composer6, (Object)compositionLocalMap, (Function2)ComposeUiNode.Companion.getSetResolvedCompositionLocals());
                    Function2 function2 = ComposeUiNode.Companion.getSetCompositeKeyHash();
                    boolean bl20 = false;
                    Composer composer7 = composer6;
                    boolean bl21 = false;
                    if (composer7.getInserting() || !Intrinsics.areEqual((Object)composer7.rememberedValue(), (Object)n9)) {
                        composer7.updateRememberedValue((Object)n9);
                        composer6.apply((Object)n9, function2);
                    }
                    Updater.set-impl((Composer)composer6, (Object)modifier2, (Function2)ComposeUiNode.Companion.getSetModifier());
                    int n11 = 0xE & $changed\14 >> 6;
                    void $composer\18 = $composer\12;
                    boolean bl22 = false;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer\18, (int)1833054614, (String)"C72@3469L9:Box.kt#2w3rfo");
                    int n12 = 6 | 0x70 & $changed\12 >> 6;
                    void var46_71 = $composer\18;
                    BoxScope boxScope = (BoxScope)BoxScopeInstance.INSTANCE;
                    boolean bl23 = false;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer\19, (int)1535767009, (String)"C2396@108899L15:GroupActivity.kt#83vr7l");
                    buttonContent.invoke((Object)boxScope, (Object)$composer\19, (Object)(0x30 | 0xE & $changed\19));
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer\19);
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer\18);
                    $composer\12.endNode();
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer\12);
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer\12);
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer\12);
                    $composer.endReplaceGroup();
                }
                if (ComposerKt.isTraceInProgress()) {
                    ComposerKt.traceEventEnd();
                }
            } else {
                $composer.skipToGroupEnd();
            }
            ScopeUpdateScope scopeUpdateScope = $composer.endRestartGroup();
            if (scopeUpdateScope == null) break block24;
            scopeUpdateScope.updateScope((arg_0, arg_1) -> GroupActivity.GroupAlbumEmptyActionButton_DTcfvLk$lambda$400(this, text, containerColor, contentColor, enabled, onClick, $changed, n, arg_0, arg_1));
        }
    }

    private final Brush groupAlbumCoverBrush(String albumId, boolean isDark) {
        Object[] objectArray = new List[6];
        Object[] objectArray2 = new Color[]{Color.box-impl((long)ColorKt.Color((long)4282090230L)), Color.box-impl((long)ColorKt.Color((long)4280110808L))};
        objectArray[0] = CollectionsKt.listOf((Object[])objectArray2);
        objectArray2 = new Color[]{Color.box-impl((long)ColorKt.Color((long)4280468830L)), Color.box-impl((long)ColorKt.Color((long)4279599165L))};
        objectArray[1] = CollectionsKt.listOf((Object[])objectArray2);
        objectArray2 = new Color[]{Color.box-impl((long)ColorKt.Color((long)0xFFEF4444L)), Color.box-impl((long)ColorKt.Color((long)4290321436L))};
        objectArray[2] = CollectionsKt.listOf((Object[])objectArray2);
        objectArray2 = new Color[]{Color.box-impl((long)ColorKt.Color((long)4294286859L)), Color.box-impl((long)ColorKt.Color((long)4290007817L))};
        objectArray[3] = CollectionsKt.listOf((Object[])objectArray2);
        objectArray2 = new Color[]{Color.box-impl((long)ColorKt.Color((long)4279548070L)), Color.box-impl((long)ColorKt.Color((long)4279203438L))};
        objectArray[4] = CollectionsKt.listOf((Object[])objectArray2);
        objectArray2 = new Color[]{Color.box-impl((long)ColorKt.Color((long)4287323382L)), Color.box-impl((long)ColorKt.Color((long)4285343961L))};
        objectArray[5] = CollectionsKt.listOf((Object[])objectArray2);
        List gradients = CollectionsKt.listOf((Object[])objectArray);
        int index = Math.abs(albumId.hashCode()) % gradients.size();
        List selected = (List)gradients.get(index);
        long start = isDark ? Color.copy-wmQWz5c$default((long)((Color)selected.get(0)).unbox-impl(), (float)0.48f, (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null) : Color.copy-wmQWz5c$default((long)((Color)selected.get(0)).unbox-impl(), (float)0.3f, (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null);
        long end = isDark ? Color.copy-wmQWz5c$default((long)((Color)selected.get(1)).unbox-impl(), (float)0.34f, (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null) : Color.copy-wmQWz5c$default((long)((Color)selected.get(1)).unbox-impl(), (float)0.22f, (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null);
        Object[] objectArray3 = new Color[]{Color.box-impl((long)start), Color.box-impl((long)end)};
        return Brush.Companion.linearGradient-mHitzGk$default((Brush.Companion)Brush.Companion, (List)CollectionsKt.listOf((Object[])objectArray3), (long)0L, (long)0L, (int)0, (int)14, null);
    }

    /*
     * Unable to fully structure code
     */
    private final Brush groupAlbumFallbackTileBrush(String categoryCode, boolean isDark) {
        var4_3 = categoryCode;
        if (var4_3 == null) ** GOTO lbl-1000
        tmp = -1;
        switch (var4_3.hashCode()) {
            case -1785494925: {
                if (var4_3.equals("LIFE_CARD")) {
                    tmp = 1;
                }
                break;
            }
            case -201149993: {
                if (var4_3.equals("LIFE_DELIVERY")) {
                    tmp = 2;
                }
                break;
            }
            case -1148168187: {
                if (var4_3.equals("WORK_SCHEDULE")) {
                    tmp = 3;
                }
                break;
            }
            case -1675026001: {
                if (var4_3.equals("LIFE_TICKET")) {
                    tmp = 4;
                }
                break;
            }
            case -1789542113: {
                if (var4_3.equals("LIFE_PICKUP")) {
                    tmp = 5;
                }
                break;
            }
            case -388293452: {
                if (var4_3.equals("WORK_TODO")) {
                    tmp = 6;
                }
                break;
            }
        }
        switch (tmp) {
            case 5: {
                var5_5 = new Color[]{Color.box-impl((long)ColorKt.Color((long)4294948986L)), Color.box-impl((long)ColorKt.Color((long)4294937165L))};
                v0 = CollectionsKt.listOf((Object[])var5_5);
                break;
            }
            case 2: {
                var5_5 = new Color[]{Color.box-impl((long)ColorKt.Color((long)0xFF88B8FFL)), Color.box-impl((long)ColorKt.Color((long)4283403519L))};
                v0 = CollectionsKt.listOf((Object[])var5_5);
                break;
            }
            case 1: {
                var5_5 = new Color[]{Color.box-impl((long)ColorKt.Color((long)4292919693L)), Color.box-impl((long)ColorKt.Color((long)4291336786L))};
                v0 = CollectionsKt.listOf((Object[])var5_5);
                break;
            }
            case 4: {
                var5_5 = new Color[]{Color.box-impl((long)ColorKt.Color((long)4290223103L)), Color.box-impl((long)ColorKt.Color((long)4287260671L))};
                v0 = CollectionsKt.listOf((Object[])var5_5);
                break;
            }
            case 6: {
                var5_5 = new Color[]{Color.box-impl((long)ColorKt.Color((long)4286374577L)), Color.box-impl((long)ColorKt.Color((long)4282632069L))};
                v0 = CollectionsKt.listOf((Object[])var5_5);
                break;
            }
            case 3: {
                var5_5 = new Color[]{Color.box-impl((long)ColorKt.Color((long)4286097919L)), Color.box-impl((long)ColorKt.Color((long)4282808309L))};
                v0 = CollectionsKt.listOf((Object[])var5_5);
                break;
            }
            default: lbl-1000:
            // 2 sources

            {
                var5_5 = new Color[]{Color.box-impl((long)ColorKt.Color((long)4290100681L)), Color.box-impl((long)ColorKt.Color((long)4287600808L))};
                v0 = CollectionsKt.listOf((Object[])var5_5);
            }
        }
        colors = v0;
        start = isDark != false ? Color.copy-wmQWz5c$default((long)((Color)colors.get(0)).unbox-impl(), (float)0.78f, (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null) : Color.copy-wmQWz5c$default((long)((Color)colors.get(0)).unbox-impl(), (float)0.92f, (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null);
        end = isDark != false ? Color.copy-wmQWz5c$default((long)((Color)colors.get(1)).unbox-impl(), (float)0.88f, (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null) : Color.copy-wmQWz5c$default((long)((Color)colors.get(1)).unbox-impl(), (float)0.98f, (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null);
        var8_8 = new Color[]{Color.box-impl((long)start), Color.box-impl((long)end)};
        return Brush.Companion.linearGradient-mHitzGk$default((Brush.Companion)Brush.Companion, (List)CollectionsKt.listOf((Object[])var8_8), (long)0L, (long)0L, (int)0, (int)14, null);
    }

    private final String groupAlbumFallbackTileLabel(MemoryRecord record) {
        String category;
        String summary;
        String title;
        if (record == null) {
            return "\u8bb0\u5fc6";
        }
        String string2 = record.getTitle();
        if (string2 == null) {
            string2 = "";
        }
        if (((CharSequence)(title = ((Object)StringsKt.trim((CharSequence)string2)).toString())).length() > 0) {
            return StringsKt.take((String)title, (int)10);
        }
        String string3 = record.getSummary();
        if (string3 == null) {
            string3 = "";
        }
        if (((CharSequence)(summary = ((Object)StringsKt.trim((CharSequence)string3)).toString())).length() > 0) {
            return StringsKt.take((String)summary, (int)10);
        }
        String string4 = record.getCategoryName();
        if (string4 == null) {
            string4 = "";
        }
        if (((CharSequence)(category = ((Object)StringsKt.trim((CharSequence)string4)).toString())).length() > 0) {
            return StringsKt.take((String)category, (int)4);
        }
        return "\u8bb0\u5fc6";
    }

    @Composable
    @ComposableTarget(applier="androidx.compose.ui.UiComposable")
    private final void GroupChip-a5Y-_hM(String text, boolean selected, long chipTextSize, Function0<Unit> onClick, Composer $composer, int $changed) {
        block8: {
            $composer = $composer.startRestartGroup(970034998);
            ComposerKt.sourceInformation((Composer)$composer, (String)"C(GroupChip)N(text,selected,chipTextSize:c#ui.unit.TextUnit,onClick)2455@111546L352:GroupActivity.kt#83vr7l");
            int $dirty = $changed;
            if (($changed & 6) == 0) {
                $dirty |= $composer.changed((Object)text) ? 4 : 2;
            }
            if (($changed & 0x30) == 0) {
                $dirty |= $composer.changed(selected) ? 32 : 16;
            }
            if (($changed & 0x180) == 0) {
                $dirty |= $composer.changed(chipTextSize) ? 256 : 128;
            }
            if (($changed & 0xC00) == 0) {
                $dirty |= $composer.changedInstance(onClick) ? 2048 : 1024;
            }
            if ($composer.shouldExecute(($dirty & 0x493) != 1170, $dirty & 1)) {
                if (ComposerKt.isTraceInProgress()) {
                    ComposerKt.traceEventStart((int)970034998, (int)$dirty, (int)-1, (String)"com.han.nomemo.GroupActivity.GroupChip (GroupActivity.kt:2454)");
                }
                TextStyle textStyle = new TextStyle(0L, chipTextSize, selected ? FontWeight.Companion.getBold() : FontWeight.Companion.getNormal(), null, null, null, null, 0L, null, null, null, 0L, null, null, null, 0, 0, 0L, null, null, null, 0, 0, null, 0xFFFFF9, null);
                int n = 16;
                boolean bl = false;
                float f = Dp.constructor-impl((float)n);
                ComposeUiKt.GlassChip-tOXsyB8(text, selected, onClick, null, f, 0.0f, false, textStyle, $composer, 0x186000 | 0xE & $dirty | 0x70 & $dirty | 0x380 & $dirty >> 3, 40);
                if (ComposerKt.isTraceInProgress()) {
                    ComposerKt.traceEventEnd();
                }
            } else {
                $composer.skipToGroupEnd();
            }
            ScopeUpdateScope scopeUpdateScope = $composer.endRestartGroup();
            if (scopeUpdateScope == null) break block8;
            scopeUpdateScope.updateScope((arg_0, arg_1) -> GroupActivity.GroupChip_a5Y__hM$lambda$401(this, text, selected, chipTextSize, onClick, $changed, arg_0, arg_1));
        }
    }

    private final String buildChipText(String label, int count) {
        return label + "(" + count + ")";
    }

    private static final String initialOpenedAlbumId_delegate$lambda$1(GroupActivity this$0) {
        String string2;
        String string3 = this$0.getIntent().getStringExtra("extra_open_album_id");
        if (string3 != null && (string3 = ((Object)StringsKt.trim((CharSequence)string3)).toString()) != null) {
            String string4;
            String string5 = string4 = string3;
            boolean bl = false;
            string2 = ((CharSequence)string5).length() > 0 ? string4 : null;
        } else {
            string2 = null;
        }
        return string2;
    }

    private static final void settingsLauncher$lambda$2(GroupActivity this$0, ActivityResult result) {
        Intrinsics.checkNotNullParameter((Object)result, (String)"result");
        if (result.getResultCode() == -1) {
            this$0.recreate();
        } else {
            this$0.refreshContent();
        }
    }

    private static final Unit onCreate$lambda$27$lambda$4$lambda$3(GroupActivity this$0, String it) {
        this$0.setSelectedCategoryCode(it);
        return Unit.INSTANCE;
    }

    private static final Unit onCreate$lambda$27$lambda$6$lambda$5(GroupActivity this$0, MemoryRecord record) {
        Intrinsics.checkNotNullParameter((Object)record, (String)"record");
        this$0.deleteRecord(record);
        return Unit.INSTANCE;
    }

    private static final Unit onCreate$lambda$27$lambda$8$lambda$7(GroupActivity this$0, Set recordIds) {
        Intrinsics.checkNotNullParameter((Object)recordIds, (String)"recordIds");
        this$0.deleteRecords(recordIds);
        return Unit.INSTANCE;
    }

    private static final Unit onCreate$lambda$27$lambda$10$lambda$9(GroupActivity this$0, MemoryRecord record) {
        Intrinsics.checkNotNullParameter((Object)record, (String)"record");
        String string2 = record.getRecordId();
        Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"getRecordId(...)");
        this$0.openDetailPage(string2);
        return Unit.INSTANCE;
    }

    private static final Unit onCreate$lambda$27$lambda$12$lambda$11(GroupActivity this$0) {
        this$0.openMemoryPage();
        return Unit.INSTANCE;
    }

    private static final Unit onCreate$lambda$27$lambda$14$lambda$13(GroupActivity this$0) {
        this$0.openReminderPage();
        return Unit.INSTANCE;
    }

    private static final Unit onCreate$lambda$27$lambda$16$lambda$15(GroupActivity this$0) {
        this$0.openSearchPage();
        return Unit.INSTANCE;
    }

    private static final Unit onCreate$lambda$27$lambda$18$lambda$17(GroupActivity this$0) {
        this$0.openSettingsPage();
        return Unit.INSTANCE;
    }

    private static final Unit onCreate$lambda$27$lambda$20$lambda$19(GroupActivity this$0) {
        this$0.setShowAddSheet(true);
        return Unit.INSTANCE;
    }

    private static final Unit onCreate$lambda$27$lambda$22$lambda$21(GroupActivity this$0) {
        this$0.setShowAddSheet(false);
        return Unit.INSTANCE;
    }

    private static final Unit onCreate$lambda$27$lambda$24$lambda$23(GroupActivity this$0, String albumId) {
        Intrinsics.checkNotNullParameter((Object)albumId, (String)"albumId");
        this$0.openAlbumDetailPage(albumId);
        return Unit.INSTANCE;
    }

    private static final Unit onCreate$lambda$27$lambda$26$lambda$25(GroupActivity this$0) {
        this$0.finish();
        return Unit.INSTANCE;
    }

    /*
     * WARNING - void declaration
     */
    @Composable
    @ComposableTarget(applier="androidx.compose.ui.UiComposable")
    private static final Unit onCreate$lambda$27(GroupActivity this$0, Composer $composer, int $changed) {
        ComposerKt.sourceInformation((Composer)$composer, (String)"C158@6918L29,159@6982L34,160@7052L41,161@7126L45,162@7204L20,163@7259L22,164@7314L20,165@7369L22,167@7467L23,168@7528L24,173@7836L43,174@7918L12,154@6714L1230:GroupActivity.kt#83vr7l");
        if ($composer.shouldExecute(($changed & 3) != 2, $changed & 1)) {
            Object object;
            void $this$cache\34;
            Object object2;
            Function1 function1;
            NoMemoDockTab noMemoDockTab;
            int n;
            String string2;
            boolean bl;
            Composer composer;
            Object object3;
            Function0 function0;
            Composer composer2;
            Object object4;
            Function0 function02;
            boolean bl2;
            Composer composer3;
            Object object5;
            Function0 function03;
            Composer composer4;
            Object object6;
            Function0 function04;
            Composer composer5;
            Object object7;
            Function0 function05;
            Composer composer6;
            Object object8;
            Function0 function06;
            Composer composer7;
            Object object9;
            Function1 function12;
            Composer composer8;
            Object object10;
            Function1 function13;
            Composer composer9;
            Object object11;
            Function1 function14;
            Composer composer10;
            Object object12;
            Function1 function15;
            GroupActivity groupActivity;
            List<MemoryRecord> list;
            boolean bl3;
            String string3;
            Composer composer11;
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart((int)-626061397, (int)$changed, (int)-1, (String)"com.han.nomemo.GroupActivity.onCreate.<anonymous> (GroupActivity.kt:154)");
            }
            GroupActivity groupActivity2 = this$0;
            List<MemoryRecord> list2 = this$0.getAllRecords();
            boolean bl4 = this$0.getHasLoadedRecords();
            String string4 = this$0.getSelectedCategoryCode();
            ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)-349574072, (String)"CC(remember):GroupActivity.kt#9igjgp");
            Composer composer12 = $composer;
            boolean bl5 = $composer.changedInstance((Object)this$0);
            boolean bl6 = false;
            Object object13 = composer11.rememberedValue();
            boolean bl7 = false;
            if (bl5 || object13 == Composer.Companion.getEmpty()) {
                string3 = string4;
                bl3 = bl4;
                list = list2;
                groupActivity = groupActivity2;
                boolean bl8 = false;
                function15 = arg_0 -> GroupActivity.onCreate$lambda$27$lambda$4$lambda$3(this$0, arg_0);
                groupActivity2 = groupActivity;
                list2 = list;
                bl4 = bl3;
                string4 = string3;
                Function1 function16 = function15;
                composer11.updateRememberedValue((Object)function16);
                object12 = function16;
            } else {
                object12 = object13;
            }
            Function1 function17 = (Function1)object12;
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
            Function1 function18 = function17;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)-349572019, (String)"CC(remember):GroupActivity.kt#9igjgp");
            composer11 = $composer;
            boolean bl9 = $composer.changedInstance((Object)this$0);
            boolean bl10 = false;
            Object object14 = composer10.rememberedValue();
            boolean bl11 = false;
            if (bl9 || object14 == Composer.Companion.getEmpty()) {
                function15 = function18;
                string3 = string4;
                bl3 = bl4;
                list = list2;
                groupActivity = groupActivity2;
                boolean bl12 = false;
                function14 = arg_0 -> GroupActivity.onCreate$lambda$27$lambda$6$lambda$5(this$0, arg_0);
                groupActivity2 = groupActivity;
                list2 = list;
                bl4 = bl3;
                string4 = string3;
                function18 = function15;
                Function1 function19 = function14;
                composer10.updateRememberedValue((Object)function19);
                object11 = function19;
            } else {
                object11 = object14;
            }
            function17 = (Function1)object11;
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
            Function1 function110 = function17;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)-349569772, (String)"CC(remember):GroupActivity.kt#9igjgp");
            composer10 = $composer;
            boolean bl13 = $composer.changedInstance((Object)this$0);
            boolean bl14 = false;
            Object object15 = composer9.rememberedValue();
            boolean bl15 = false;
            if (bl13 || object15 == Composer.Companion.getEmpty()) {
                function14 = function110;
                function15 = function18;
                string3 = string4;
                bl3 = bl4;
                list = list2;
                groupActivity = groupActivity2;
                boolean bl16 = false;
                function13 = arg_0 -> GroupActivity.onCreate$lambda$27$lambda$8$lambda$7(this$0, arg_0);
                groupActivity2 = groupActivity;
                list2 = list;
                bl4 = bl3;
                string4 = string3;
                function18 = function15;
                function110 = function14;
                Function1 function111 = function13;
                composer9.updateRememberedValue((Object)function111);
                object10 = function111;
            } else {
                object10 = object15;
            }
            function17 = (Function1)object10;
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
            Function1 function112 = function17;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)-349567400, (String)"CC(remember):GroupActivity.kt#9igjgp");
            composer9 = $composer;
            boolean bl17 = $composer.changedInstance((Object)this$0);
            boolean bl18 = false;
            Object object16 = composer8.rememberedValue();
            boolean bl19 = false;
            if (bl17 || object16 == Composer.Companion.getEmpty()) {
                function13 = function112;
                function14 = function110;
                function15 = function18;
                string3 = string4;
                bl3 = bl4;
                list = list2;
                groupActivity = groupActivity2;
                boolean bl20 = false;
                function12 = arg_0 -> GroupActivity.onCreate$lambda$27$lambda$10$lambda$9(this$0, arg_0);
                groupActivity2 = groupActivity;
                list2 = list;
                bl4 = bl3;
                string4 = string3;
                function18 = function15;
                function110 = function14;
                function112 = function13;
                Function1 function113 = function12;
                composer8.updateRememberedValue((Object)function113);
                object9 = function113;
            } else {
                object9 = object16;
            }
            function17 = (Function1)object9;
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
            Function1 function114 = function17;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)-349564929, (String)"CC(remember):GroupActivity.kt#9igjgp");
            composer8 = $composer;
            boolean bl21 = $composer.changedInstance((Object)this$0);
            boolean bl22 = false;
            Object object17 = composer7.rememberedValue();
            boolean bl23 = false;
            if (bl21 || object17 == Composer.Companion.getEmpty()) {
                function12 = function114;
                function13 = function112;
                function14 = function110;
                function15 = function18;
                string3 = string4;
                bl3 = bl4;
                list = list2;
                groupActivity = groupActivity2;
                boolean bl24 = false;
                function06 = () -> GroupActivity.onCreate$lambda$27$lambda$12$lambda$11(this$0);
                groupActivity2 = groupActivity;
                list2 = list;
                bl4 = bl3;
                string4 = string3;
                function18 = function15;
                function110 = function14;
                function112 = function13;
                function114 = function12;
                Function0 function07 = function06;
                composer7.updateRememberedValue((Object)function07);
                object8 = function07;
            } else {
                object8 = object17;
            }
            function17 = (Function0)object8;
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
            Function1 function115 = function17;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)-349563167, (String)"CC(remember):GroupActivity.kt#9igjgp");
            composer7 = $composer;
            boolean bl25 = $composer.changedInstance((Object)this$0);
            boolean bl26 = false;
            Object object18 = composer6.rememberedValue();
            boolean bl27 = false;
            if (bl25 || object18 == Composer.Companion.getEmpty()) {
                function06 = function115;
                function12 = function114;
                function13 = function112;
                function14 = function110;
                function15 = function18;
                string3 = string4;
                bl3 = bl4;
                list = list2;
                groupActivity = groupActivity2;
                boolean bl28 = false;
                function05 = () -> GroupActivity.onCreate$lambda$27$lambda$14$lambda$13(this$0);
                groupActivity2 = groupActivity;
                list2 = list;
                bl4 = bl3;
                string4 = string3;
                function18 = function15;
                function110 = function14;
                function112 = function13;
                function114 = function12;
                function115 = function06;
                Function0 function08 = function05;
                composer6.updateRememberedValue((Object)function08);
                object7 = function08;
            } else {
                object7 = object18;
            }
            function17 = (Function0)object7;
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
            Function1 function116 = function17;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)-349561409, (String)"CC(remember):GroupActivity.kt#9igjgp");
            composer6 = $composer;
            boolean bl29 = $composer.changedInstance((Object)this$0);
            boolean bl30 = false;
            Object object19 = composer5.rememberedValue();
            boolean bl31 = false;
            if (bl29 || object19 == Composer.Companion.getEmpty()) {
                function05 = function116;
                function06 = function115;
                function12 = function114;
                function13 = function112;
                function14 = function110;
                function15 = function18;
                string3 = string4;
                bl3 = bl4;
                list = list2;
                groupActivity = groupActivity2;
                boolean bl32 = false;
                function04 = () -> GroupActivity.onCreate$lambda$27$lambda$16$lambda$15(this$0);
                groupActivity2 = groupActivity;
                list2 = list;
                bl4 = bl3;
                string4 = string3;
                function18 = function15;
                function110 = function14;
                function112 = function13;
                function114 = function12;
                function115 = function06;
                function116 = function05;
                Function0 function09 = function04;
                composer5.updateRememberedValue((Object)function09);
                object6 = function09;
            } else {
                object6 = object19;
            }
            function17 = (Function0)object6;
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
            Function1 function117 = function17;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)-349559647, (String)"CC(remember):GroupActivity.kt#9igjgp");
            composer5 = $composer;
            boolean bl33 = $composer.changedInstance((Object)this$0);
            boolean bl34 = false;
            Object object20 = composer4.rememberedValue();
            boolean bl35 = false;
            if (bl33 || object20 == Composer.Companion.getEmpty()) {
                function04 = function117;
                function05 = function116;
                function06 = function115;
                function12 = function114;
                function13 = function112;
                function14 = function110;
                function15 = function18;
                string3 = string4;
                bl3 = bl4;
                list = list2;
                groupActivity = groupActivity2;
                boolean bl36 = false;
                function03 = () -> GroupActivity.onCreate$lambda$27$lambda$18$lambda$17(this$0);
                groupActivity2 = groupActivity;
                list2 = list;
                bl4 = bl3;
                string4 = string3;
                function18 = function15;
                function110 = function14;
                function112 = function13;
                function114 = function12;
                function115 = function06;
                function116 = function05;
                function117 = function04;
                Function0 function010 = function03;
                composer4.updateRememberedValue((Object)function010);
                object5 = function010;
            } else {
                object5 = object20;
            }
            function17 = (Function0)object5;
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
            Function1 function118 = function17;
            boolean bl37 = this$0.getShowAddSheet();
            ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)-349556510, (String)"CC(remember):GroupActivity.kt#9igjgp");
            composer4 = $composer;
            boolean bl38 = $composer.changedInstance((Object)this$0);
            boolean bl39 = false;
            Object object21 = composer3.rememberedValue();
            boolean bl40 = false;
            if (bl38 || object21 == Composer.Companion.getEmpty()) {
                bl2 = bl37;
                function03 = function118;
                function04 = function117;
                function05 = function116;
                function06 = function115;
                function12 = function114;
                function13 = function112;
                function14 = function110;
                function15 = function18;
                string3 = string4;
                bl3 = bl4;
                list = list2;
                groupActivity = groupActivity2;
                boolean bl41 = false;
                function02 = () -> GroupActivity.onCreate$lambda$27$lambda$20$lambda$19(this$0);
                groupActivity2 = groupActivity;
                list2 = list;
                bl4 = bl3;
                string4 = string3;
                function18 = function15;
                function110 = function14;
                function112 = function13;
                function114 = function12;
                function115 = function06;
                function116 = function05;
                function117 = function04;
                function118 = function03;
                bl37 = bl2;
                Function0 function011 = function02;
                composer3.updateRememberedValue((Object)function011);
                object4 = function011;
            } else {
                object4 = object21;
            }
            function17 = (Function0)object4;
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
            Function1 function119 = function17;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)-349554557, (String)"CC(remember):GroupActivity.kt#9igjgp");
            composer3 = $composer;
            boolean bl42 = $composer.changedInstance((Object)this$0);
            boolean bl43 = false;
            Object object22 = composer2.rememberedValue();
            boolean bl44 = false;
            if (bl42 || object22 == Composer.Companion.getEmpty()) {
                function02 = function119;
                bl2 = bl37;
                function03 = function118;
                function04 = function117;
                function05 = function116;
                function06 = function115;
                function12 = function114;
                function13 = function112;
                function14 = function110;
                function15 = function18;
                string3 = string4;
                bl3 = bl4;
                list = list2;
                groupActivity = groupActivity2;
                boolean bl45 = false;
                function0 = () -> GroupActivity.onCreate$lambda$27$lambda$22$lambda$21(this$0);
                groupActivity2 = groupActivity;
                list2 = list;
                bl4 = bl3;
                string4 = string3;
                function18 = function15;
                function110 = function14;
                function112 = function13;
                function114 = function12;
                function115 = function06;
                function116 = function05;
                function117 = function04;
                function118 = function03;
                bl37 = bl2;
                function119 = function02;
                Function0 function012 = function0;
                composer2.updateRememberedValue((Object)function012);
                object3 = function012;
            } else {
                object3 = object22;
            }
            function17 = (Function0)object3;
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
            Function1 function120 = function17;
            NoMemoDockTab noMemoDockTab2 = this$0.startupDockPulseTab;
            int n2 = this$0.getAlbumRefreshTick();
            String string5 = this$0.getInitialOpenedAlbumId();
            boolean bl46 = this$0.getInitialOpenedAlbumId() != null;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)-349544682, (String)"CC(remember):GroupActivity.kt#9igjgp");
            composer2 = $composer;
            boolean bl47 = $composer.changedInstance((Object)this$0);
            boolean bl48 = false;
            Object object23 = composer.rememberedValue();
            boolean bl49 = false;
            if (bl47 || object23 == Composer.Companion.getEmpty()) {
                bl = bl46;
                string2 = string5;
                n = n2;
                noMemoDockTab = noMemoDockTab2;
                function0 = function120;
                function02 = function119;
                bl2 = bl37;
                function03 = function118;
                function04 = function117;
                function05 = function116;
                function06 = function115;
                function12 = function114;
                function13 = function112;
                function14 = function110;
                function15 = function18;
                string3 = string4;
                bl3 = bl4;
                list = list2;
                groupActivity = groupActivity2;
                boolean bl50 = false;
                function1 = arg_0 -> GroupActivity.onCreate$lambda$27$lambda$24$lambda$23(this$0, arg_0);
                groupActivity2 = groupActivity;
                list2 = list;
                bl4 = bl3;
                string4 = string3;
                function18 = function15;
                function110 = function14;
                function112 = function13;
                function114 = function12;
                function115 = function06;
                function116 = function05;
                function117 = function04;
                function118 = function03;
                bl37 = bl2;
                function119 = function02;
                function120 = function0;
                noMemoDockTab2 = noMemoDockTab;
                n2 = n;
                string5 = string2;
                bl46 = bl;
                Function1 function121 = function1;
                composer.updateRememberedValue((Object)function121);
                object2 = function121;
            } else {
                object2 = object23;
            }
            function17 = (Function1)object2;
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
            Function1 function122 = function17;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)-349542089, (String)"CC(remember):GroupActivity.kt#9igjgp");
            composer = $composer;
            boolean bl51 = $composer.changedInstance((Object)this$0);
            boolean bl52 = false;
            Object object24 = $this$cache\34.rememberedValue();
            boolean bl53 = false;
            if (bl51 || object24 == Composer.Companion.getEmpty()) {
                function1 = function122;
                bl = bl46;
                string2 = string5;
                n = n2;
                noMemoDockTab = noMemoDockTab2;
                function0 = function120;
                function02 = function119;
                bl2 = bl37;
                function03 = function118;
                function04 = function117;
                function05 = function116;
                function06 = function115;
                function12 = function114;
                function13 = function112;
                function14 = function110;
                function15 = function18;
                string3 = string4;
                bl3 = bl4;
                list = list2;
                groupActivity = groupActivity2;
                boolean bl54 = false;
                Function0 function013 = () -> GroupActivity.onCreate$lambda$27$lambda$26$lambda$25(this$0);
                groupActivity2 = groupActivity;
                list2 = list;
                bl4 = bl3;
                string4 = string3;
                function18 = function15;
                function110 = function14;
                function112 = function13;
                function114 = function12;
                function115 = function06;
                function116 = function05;
                function117 = function04;
                function118 = function03;
                bl37 = bl2;
                function119 = function02;
                function120 = function0;
                noMemoDockTab2 = noMemoDockTab;
                n2 = n;
                string5 = string2;
                bl46 = bl;
                function122 = function1;
                Function0 function014 = function013;
                $this$cache\34.updateRememberedValue((Object)function014);
                object = function014;
            } else {
                object = object24;
            }
            function17 = (Function0)object;
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
            groupActivity2.GroupContent(list2, bl4, string4, (Function1<? super String, Unit>)function18, (Function1<? super MemoryRecord, Unit>)function110, (Function1<? super Set<String>, Unit>)function112, (Function1<? super MemoryRecord, Unit>)function114, (Function0<Unit>)function115, (Function0<Unit>)function116, (Function0<Unit>)function117, (Function0<Unit>)function118, bl37, (Function0<Unit>)function119, (Function0<Unit>)function120, noMemoDockTab2, n2, string5, bl46, (Function1<? super String, Unit>)function122, (Function0<Unit>)function17, $composer, 0, 0, 0);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        } else {
            $composer.skipToGroupEnd();
        }
        return Unit.INSTANCE;
    }

    /*
     * WARNING - void declaration
     */
    private static final List<GroupAlbumStore.GroupAlbum> GroupContent$lambda$34(MutableState<List<GroupAlbumStore.GroupAlbum>> $albumList$delegate) {
        void $this$getValue\1;
        State state = (State)$albumList$delegate;
        Object var2_2 = null;
        Object property\1 = null;
        boolean bl = false;
        return (List)$this$getValue\1.getValue();
    }

    /*
     * WARNING - void declaration
     */
    private static final void GroupContent$lambda$35(MutableState<List<GroupAlbumStore.GroupAlbum>> $albumList$delegate, List<GroupAlbumStore.GroupAlbum> list) {
        void $this$setValue\1;
        MutableState<List<GroupAlbumStore.GroupAlbum>> mutableState = $albumList$delegate;
        Object var3_3 = null;
        Object var4_4 = null;
        List<GroupAlbumStore.GroupAlbum> list2 = list;
        boolean bl = false;
        $this$setValue\1.setValue(list2);
    }

    /*
     * WARNING - void declaration
     */
    private static final String GroupContent$lambda$37(MutableState<String> $openedAlbumId$delegate) {
        void $this$getValue\1;
        State state = (State)$openedAlbumId$delegate;
        Object var2_2 = null;
        Object property\1 = null;
        boolean bl = false;
        return (String)$this$getValue\1.getValue();
    }

    /*
     * WARNING - void declaration
     */
    private static final void GroupContent$lambda$38(MutableState<String> $openedAlbumId$delegate, String string2) {
        void $this$setValue\1;
        MutableState<String> mutableState = $openedAlbumId$delegate;
        Object var3_3 = null;
        Object var4_4 = null;
        String string3 = string2;
        boolean bl = false;
        $this$setValue\1.setValue((Object)string3);
    }

    /*
     * WARNING - void declaration
     */
    private static final boolean GroupContent$lambda$40(MutableState<Boolean> $showCreateAlbumDialog$delegate) {
        void $this$getValue\1;
        State state = (State)$showCreateAlbumDialog$delegate;
        Object var2_2 = null;
        Object property\1 = null;
        boolean bl = false;
        return (Boolean)$this$getValue\1.getValue();
    }

    /*
     * WARNING - void declaration
     */
    private static final void GroupContent$lambda$41(MutableState<Boolean> $showCreateAlbumDialog$delegate, boolean bl) {
        void $this$setValue\1;
        MutableState<Boolean> mutableState = $showCreateAlbumDialog$delegate;
        Object var3_3 = null;
        Object var4_4 = null;
        Boolean bl2 = bl;
        boolean bl3 = false;
        $this$setValue\1.setValue((Object)bl2);
    }

    /*
     * WARNING - void declaration
     */
    private static final boolean GroupContent$lambda$43(MutableState<Boolean> $showAddExistingSheet$delegate) {
        void $this$getValue\1;
        State state = (State)$showAddExistingSheet$delegate;
        Object var2_2 = null;
        Object property\1 = null;
        boolean bl = false;
        return (Boolean)$this$getValue\1.getValue();
    }

    /*
     * WARNING - void declaration
     */
    private static final void GroupContent$lambda$44(MutableState<Boolean> $showAddExistingSheet$delegate, boolean bl) {
        void $this$setValue\1;
        MutableState<Boolean> mutableState = $showAddExistingSheet$delegate;
        Object var3_3 = null;
        Object var4_4 = null;
        Boolean bl2 = bl;
        boolean bl3 = false;
        $this$setValue\1.setValue((Object)bl2);
    }

    /*
     * WARNING - void declaration
     */
    private static final Set<String> GroupContent$lambda$46(MutableState<Set<String>> $selectedExistingRecordIds$delegate) {
        void $this$getValue\1;
        State state = (State)$selectedExistingRecordIds$delegate;
        Object var2_2 = null;
        Object property\1 = null;
        boolean bl = false;
        return (Set)$this$getValue\1.getValue();
    }

    /*
     * WARNING - void declaration
     */
    private static final void GroupContent$lambda$47(MutableState<Set<String>> $selectedExistingRecordIds$delegate, Set<String> set) {
        void $this$setValue\1;
        MutableState<Set<String>> mutableState = $selectedExistingRecordIds$delegate;
        Object var3_3 = null;
        Object var4_4 = null;
        Set<String> set2 = set;
        boolean bl = false;
        $this$setValue\1.setValue(set2);
    }

    /*
     * WARNING - void declaration
     */
    private static final String GroupContent$lambda$49(MutableState<String> $addExistingSearchQuery$delegate) {
        void $this$getValue\1;
        State state = (State)$addExistingSearchQuery$delegate;
        Object var2_2 = null;
        Object property\1 = null;
        boolean bl = false;
        return (String)$this$getValue\1.getValue();
    }

    /*
     * WARNING - void declaration
     */
    private static final void GroupContent$lambda$50(MutableState<String> $addExistingSearchQuery$delegate, String string2) {
        void $this$setValue\1;
        MutableState<String> mutableState = $addExistingSearchQuery$delegate;
        Object var3_3 = null;
        Object var4_4 = null;
        String string3 = string2;
        boolean bl = false;
        $this$setValue\1.setValue((Object)string3);
    }

    /*
     * WARNING - void declaration
     */
    private static final boolean GroupContent$lambda$52(MutableState<Boolean> $groupListMoreExpanded$delegate) {
        void $this$getValue\1;
        State state = (State)$groupListMoreExpanded$delegate;
        Object var2_2 = null;
        Object property\1 = null;
        boolean bl = false;
        return (Boolean)$this$getValue\1.getValue();
    }

    /*
     * WARNING - void declaration
     */
    private static final void GroupContent$lambda$53(MutableState<Boolean> $groupListMoreExpanded$delegate, boolean bl) {
        void $this$setValue\1;
        MutableState<Boolean> mutableState = $groupListMoreExpanded$delegate;
        Object var3_3 = null;
        Object var4_4 = null;
        Boolean bl2 = bl;
        boolean bl3 = false;
        $this$setValue\1.setValue((Object)bl2);
    }

    /*
     * WARNING - void declaration
     */
    private static final boolean GroupContent$lambda$55(MutableState<Boolean> $detailMoreExpanded$delegate) {
        void $this$getValue\1;
        State state = (State)$detailMoreExpanded$delegate;
        Object var2_2 = null;
        Object property\1 = null;
        boolean bl = false;
        return (Boolean)$this$getValue\1.getValue();
    }

    /*
     * WARNING - void declaration
     */
    private static final void GroupContent$lambda$56(MutableState<Boolean> $detailMoreExpanded$delegate, boolean bl) {
        void $this$setValue\1;
        MutableState<Boolean> mutableState = $detailMoreExpanded$delegate;
        Object var3_3 = null;
        Object var4_4 = null;
        Boolean bl2 = bl;
        boolean bl3 = false;
        $this$setValue\1.setValue((Object)bl2);
    }

    /*
     * WARNING - void declaration
     */
    private static final IntRect GroupContent$lambda$58(MutableState<IntRect> $groupListMoreAnchorBounds$delegate) {
        void $this$getValue\1;
        State state = (State)$groupListMoreAnchorBounds$delegate;
        Object var2_2 = null;
        Object property\1 = null;
        boolean bl = false;
        return (IntRect)$this$getValue\1.getValue();
    }

    /*
     * WARNING - void declaration
     */
    private static final void GroupContent$lambda$59(MutableState<IntRect> $groupListMoreAnchorBounds$delegate, IntRect intRect) {
        void $this$setValue\1;
        MutableState<IntRect> mutableState = $groupListMoreAnchorBounds$delegate;
        Object var3_3 = null;
        Object var4_4 = null;
        IntRect intRect2 = intRect;
        boolean bl = false;
        $this$setValue\1.setValue((Object)intRect2);
    }

    /*
     * WARNING - void declaration
     */
    private static final IntRect GroupContent$lambda$61(MutableState<IntRect> $detailMoreAnchorBounds$delegate) {
        void $this$getValue\1;
        State state = (State)$detailMoreAnchorBounds$delegate;
        Object var2_2 = null;
        Object property\1 = null;
        boolean bl = false;
        return (IntRect)$this$getValue\1.getValue();
    }

    /*
     * WARNING - void declaration
     */
    private static final void GroupContent$lambda$62(MutableState<IntRect> $detailMoreAnchorBounds$delegate, IntRect intRect) {
        void $this$setValue\1;
        MutableState<IntRect> mutableState = $detailMoreAnchorBounds$delegate;
        Object var3_3 = null;
        Object var4_4 = null;
        IntRect intRect2 = intRect;
        boolean bl = false;
        $this$setValue\1.setValue((Object)intRect2);
    }

    /*
     * WARNING - void declaration
     */
    private static final Set<String> GroupContent$lambda$64(MutableState<Set<String>> $selectedAlbumRecordIds$delegate) {
        void $this$getValue\1;
        State state = (State)$selectedAlbumRecordIds$delegate;
        Object var2_2 = null;
        Object property\1 = null;
        boolean bl = false;
        return (Set)$this$getValue\1.getValue();
    }

    /*
     * WARNING - void declaration
     */
    private static final void GroupContent$lambda$65(MutableState<Set<String>> $selectedAlbumRecordIds$delegate, Set<String> set) {
        void $this$setValue\1;
        MutableState<Set<String>> mutableState = $selectedAlbumRecordIds$delegate;
        Object var3_3 = null;
        Object var4_4 = null;
        Set<String> set2 = set;
        boolean bl = false;
        $this$setValue\1.setValue(set2);
    }

    /*
     * WARNING - void declaration
     */
    private static final boolean GroupContent$lambda$67(MutableState<Boolean> $albumSelectionModeActive$delegate) {
        void $this$getValue\1;
        State state = (State)$albumSelectionModeActive$delegate;
        Object var2_2 = null;
        Object property\1 = null;
        boolean bl = false;
        return (Boolean)$this$getValue\1.getValue();
    }

    /*
     * WARNING - void declaration
     */
    private static final void GroupContent$lambda$68(MutableState<Boolean> $albumSelectionModeActive$delegate, boolean bl) {
        void $this$setValue\1;
        MutableState<Boolean> mutableState = $albumSelectionModeActive$delegate;
        Object var3_3 = null;
        Object var4_4 = null;
        Boolean bl2 = bl;
        boolean bl3 = false;
        $this$setValue\1.setValue((Object)bl2);
    }

    /*
     * WARNING - void declaration
     */
    private static final boolean GroupContent$lambda$70(MutableState<Boolean> $showRemoveFromAlbumConfirm$delegate) {
        void $this$getValue\1;
        State state = (State)$showRemoveFromAlbumConfirm$delegate;
        Object var2_2 = null;
        Object property\1 = null;
        boolean bl = false;
        return (Boolean)$this$getValue\1.getValue();
    }

    /*
     * WARNING - void declaration
     */
    private static final void GroupContent$lambda$71(MutableState<Boolean> $showRemoveFromAlbumConfirm$delegate, boolean bl) {
        void $this$setValue\1;
        MutableState<Boolean> mutableState = $showRemoveFromAlbumConfirm$delegate;
        Object var3_3 = null;
        Object var4_4 = null;
        Boolean bl2 = bl;
        boolean bl3 = false;
        $this$setValue\1.setValue((Object)bl2);
    }

    /*
     * WARNING - void declaration
     */
    private static final boolean GroupContent$lambda$73(MutableState<Boolean> $showDeleteSelectedConfirm$delegate) {
        void $this$getValue\1;
        State state = (State)$showDeleteSelectedConfirm$delegate;
        Object var2_2 = null;
        Object property\1 = null;
        boolean bl = false;
        return (Boolean)$this$getValue\1.getValue();
    }

    /*
     * WARNING - void declaration
     */
    private static final void GroupContent$lambda$74(MutableState<Boolean> $showDeleteSelectedConfirm$delegate, boolean bl) {
        void $this$setValue\1;
        MutableState<Boolean> mutableState = $showDeleteSelectedConfirm$delegate;
        Object var3_3 = null;
        Object var4_4 = null;
        Boolean bl2 = bl;
        boolean bl3 = false;
        $this$setValue\1.setValue((Object)bl2);
    }

    /*
     * WARNING - void declaration
     */
    private static final boolean GroupContent$lambda$76(MutableState<Boolean> $showEditAlbumDialog$delegate) {
        void $this$getValue\1;
        State state = (State)$showEditAlbumDialog$delegate;
        Object var2_2 = null;
        Object property\1 = null;
        boolean bl = false;
        return (Boolean)$this$getValue\1.getValue();
    }

    /*
     * WARNING - void declaration
     */
    private static final void GroupContent$lambda$77(MutableState<Boolean> $showEditAlbumDialog$delegate, boolean bl) {
        void $this$setValue\1;
        MutableState<Boolean> mutableState = $showEditAlbumDialog$delegate;
        Object var3_3 = null;
        Object var4_4 = null;
        Boolean bl2 = bl;
        boolean bl3 = false;
        $this$setValue\1.setValue((Object)bl2);
    }

    /*
     * WARNING - void declaration
     */
    private static final boolean GroupContent$lambda$79(MutableState<Boolean> $showDeleteAlbumConfirm$delegate) {
        void $this$getValue\1;
        State state = (State)$showDeleteAlbumConfirm$delegate;
        Object var2_2 = null;
        Object property\1 = null;
        boolean bl = false;
        return (Boolean)$this$getValue\1.getValue();
    }

    /*
     * WARNING - void declaration
     */
    private static final void GroupContent$lambda$80(MutableState<Boolean> $showDeleteAlbumConfirm$delegate, boolean bl) {
        void $this$setValue\1;
        MutableState<Boolean> mutableState = $showDeleteAlbumConfirm$delegate;
        Object var3_3 = null;
        Object var4_4 = null;
        Boolean bl2 = bl;
        boolean bl3 = false;
        $this$setValue\1.setValue((Object)bl2);
    }

    /*
     * WARNING - void declaration
     */
    private static final boolean GroupContent$lambda$82(MutableState<Boolean> $closingStandaloneDetail$delegate) {
        void $this$getValue\1;
        State state = (State)$closingStandaloneDetail$delegate;
        Object var2_2 = null;
        Object property\1 = null;
        boolean bl = false;
        return (Boolean)$this$getValue\1.getValue();
    }

    /*
     * WARNING - void declaration
     */
    private static final void GroupContent$lambda$83(MutableState<Boolean> $closingStandaloneDetail$delegate, boolean bl) {
        void $this$setValue\1;
        MutableState<Boolean> mutableState = $closingStandaloneDetail$delegate;
        Object var3_3 = null;
        Object var4_4 = null;
        Boolean bl2 = bl;
        boolean bl3 = false;
        $this$setValue\1.setValue((Object)bl2);
    }

    /*
     * WARNING - void declaration
     */
    private static final String GroupContent$lambda$85(MutableState<String> $editingAlbumId$delegate) {
        void $this$getValue\1;
        State state = (State)$editingAlbumId$delegate;
        Object var2_2 = null;
        Object property\1 = null;
        boolean bl = false;
        return (String)$this$getValue\1.getValue();
    }

    /*
     * WARNING - void declaration
     */
    private static final void GroupContent$lambda$86(MutableState<String> $editingAlbumId$delegate, String string2) {
        void $this$setValue\1;
        MutableState<String> mutableState = $editingAlbumId$delegate;
        Object var3_3 = null;
        Object var4_4 = null;
        String string3 = string2;
        boolean bl = false;
        $this$setValue\1.setValue((Object)string3);
    }

    /*
     * WARNING - void declaration
     */
    private static final String GroupContent$lambda$88(MutableState<String> $albumNameInput$delegate) {
        void $this$getValue\1;
        State state = (State)$albumNameInput$delegate;
        Object var2_2 = null;
        Object property\1 = null;
        boolean bl = false;
        return (String)$this$getValue\1.getValue();
    }

    /*
     * WARNING - void declaration
     */
    private static final void GroupContent$lambda$89(MutableState<String> $albumNameInput$delegate, String string2) {
        void $this$setValue\1;
        MutableState<String> mutableState = $albumNameInput$delegate;
        Object var3_3 = null;
        Object var4_4 = null;
        String string3 = string2;
        boolean bl = false;
        $this$setValue\1.setValue((Object)string3);
    }

    /*
     * WARNING - void declaration
     */
    private static final String GroupContent$lambda$91(MutableState<String> $albumDescriptionInput$delegate) {
        void $this$getValue\1;
        State state = (State)$albumDescriptionInput$delegate;
        Object var2_2 = null;
        Object property\1 = null;
        boolean bl = false;
        return (String)$this$getValue\1.getValue();
    }

    /*
     * WARNING - void declaration
     */
    private static final void GroupContent$lambda$92(MutableState<String> $albumDescriptionInput$delegate, String string2) {
        void $this$setValue\1;
        MutableState<String> mutableState = $albumDescriptionInput$delegate;
        Object var3_3 = null;
        Object var4_4 = null;
        String string3 = string2;
        boolean bl = false;
        $this$setValue\1.setValue((Object)string3);
    }

    /*
     * WARNING - void declaration
     */
    private static final boolean GroupContent$lambda$94(MutableState<Boolean> $albumAutoClassifyEnabledInput$delegate) {
        void $this$getValue\1;
        State state = (State)$albumAutoClassifyEnabledInput$delegate;
        Object var2_2 = null;
        Object property\1 = null;
        boolean bl = false;
        return (Boolean)$this$getValue\1.getValue();
    }

    /*
     * WARNING - void declaration
     */
    private static final void GroupContent$lambda$95(MutableState<Boolean> $albumAutoClassifyEnabledInput$delegate, boolean bl) {
        void $this$setValue\1;
        MutableState<Boolean> mutableState = $albumAutoClassifyEnabledInput$delegate;
        Object var3_3 = null;
        Object var4_4 = null;
        Boolean bl2 = bl;
        boolean bl3 = false;
        $this$setValue\1.setValue((Object)bl2);
    }

    private static final CharSequence GroupContent$lambda$119$lambda$118$lambda$117(String it) {
        String string2 = it;
        if (string2 == null) {
            string2 = "";
        }
        return string2;
    }

    private static final float GroupContent$lambda$122$lambda$121(GroupAlbumStore.GroupAlbum $openedAlbum, LazyListState $groupListState, float $groupHeaderCollapseDistancePx, MutableState $albumList$delegate) {
        return $openedAlbum != null || GroupActivity.GroupContent$lambda$34((MutableState<List<GroupAlbumStore.GroupAlbum>>)$albumList$delegate).isEmpty() ? 0.0f : ($groupListState.getFirstVisibleItemIndex() > 0 ? 1.0f : ($groupHeaderCollapseDistancePx <= 0.0f ? 0.0f : RangesKt.coerceIn((float)((float)$groupListState.getFirstVisibleItemScrollOffset() / $groupHeaderCollapseDistancePx), (float)0.0f, (float)1.0f)));
    }

    /*
     * WARNING - void declaration
     */
    private static final float GroupContent$lambda$123(State<Float> $groupHeaderCollapseTarget$delegate) {
        void $this$getValue\1;
        State<Float> state = $groupHeaderCollapseTarget$delegate;
        Object var2_2 = null;
        Object property\1 = null;
        boolean bl = false;
        return ((Number)$this$getValue\1.getValue()).floatValue();
    }

    /*
     * WARNING - void declaration
     */
    private static final float GroupContent$lambda$124(State<Float> $groupHeaderCollapseProgress$delegate) {
        void $this$getValue\1;
        State<Float> state = $groupHeaderCollapseProgress$delegate;
        Object var2_2 = null;
        Object property\1 = null;
        boolean bl = false;
        return ((Number)$this$getValue\1.getValue()).floatValue();
    }

    /*
     * WARNING - void declaration
     */
    private static final float GroupContent$lambda$126(State<Dp> $groupExpandedTitleHeight$delegate) {
        void $this$getValue\1;
        State<Dp> state = $groupExpandedTitleHeight$delegate;
        Object var2_2 = null;
        Object property\1 = null;
        boolean bl = false;
        return ((Dp)$this$getValue\1.getValue()).unbox-impl();
    }

    /*
     * WARNING - void declaration
     */
    private static final float GroupContent$lambda$127(State<Dp> $groupListTopPadding$delegate) {
        void $this$getValue\1;
        State<Dp> state = $groupListTopPadding$delegate;
        Object var2_2 = null;
        Object property\1 = null;
        boolean bl = false;
        return ((Dp)$this$getValue\1.getValue()).unbox-impl();
    }

    private static final Unit GroupContent$lambda$133$lambda$132(boolean $openedAsStandaloneDetail, Function0 $onCloseAlbumDetail, GroupActivity this$0, MutableState $openedAlbumId$delegate) {
        if ($openedAsStandaloneDetail) {
            $onCloseAlbumDetail.invoke();
        } else {
            GroupActivity.GroupContent$lambda$38((MutableState<String>)$openedAlbumId$delegate, null);
        }
        this$0.resetDoubleBackExitState();
        return Unit.INSTANCE;
    }

    private static final Unit GroupContent$lambda$135$lambda$134(MutableState $albumSelectionModeActive$delegate, MutableState $selectedAlbumRecordIds$delegate, MutableState $showRemoveFromAlbumConfirm$delegate, MutableState $showDeleteSelectedConfirm$delegate) {
        GroupActivity.GroupContent$lambda$68((MutableState<Boolean>)$albumSelectionModeActive$delegate, false);
        GroupActivity.GroupContent$lambda$65((MutableState<Set<String>>)$selectedAlbumRecordIds$delegate, SetsKt.emptySet());
        GroupActivity.GroupContent$lambda$71((MutableState<Boolean>)$showRemoveFromAlbumConfirm$delegate, false);
        GroupActivity.GroupContent$lambda$74((MutableState<Boolean>)$showDeleteSelectedConfirm$delegate, false);
        return Unit.INSTANCE;
    }

    private static final Unit GroupContent$lambda$138$lambda$137(MutableState $detailMoreExpanded$delegate) {
        GroupActivity.GroupContent$lambda$56((MutableState<Boolean>)$detailMoreExpanded$delegate, false);
        return Unit.INSTANCE;
    }

    private static final Unit GroupContent$lambda$140$lambda$139(MutableState $groupListMoreExpanded$delegate) {
        GroupActivity.GroupContent$lambda$53((MutableState<Boolean>)$groupListMoreExpanded$delegate, false);
        return Unit.INSTANCE;
    }

    private static final Unit GroupContent$lambda$259$lambda$258$lambda$257$lambda$142$lambda$141(ContentDrawScope $this$rememberLayerBackdrop) {
        Intrinsics.checkNotNullParameter((Object)$this$rememberLayerBackdrop, (String)"$this$rememberLayerBackdrop");
        $this$rememberLayerBackdrop.drawContent();
        return Unit.INSTANCE;
    }

    private static final Unit GroupContent$lambda$259$lambda$258$lambda$257$lambda$188$lambda$151$lambda$144$lambda$143(Function0 $onOpenSearch, MutableState $groupListMoreExpanded$delegate) {
        GroupActivity.GroupContent$lambda$53((MutableState<Boolean>)$groupListMoreExpanded$delegate, false);
        $onOpenSearch.invoke();
        return Unit.INSTANCE;
    }

    private static final Unit GroupContent$lambda$259$lambda$258$lambda$257$lambda$188$lambda$151$lambda$146$lambda$145(MutableState $groupListMoreExpanded$delegate) {
        GroupActivity.GroupContent$lambda$53((MutableState<Boolean>)$groupListMoreExpanded$delegate, !GroupActivity.GroupContent$lambda$52((MutableState<Boolean>)$groupListMoreExpanded$delegate));
        return Unit.INSTANCE;
    }

    private static final Unit GroupContent$lambda$259$lambda$258$lambda$257$lambda$188$lambda$151$lambda$148$lambda$147(MutableState $groupListMoreAnchorBounds$delegate, IntRect it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        GroupActivity.GroupContent$lambda$59((MutableState<IntRect>)$groupListMoreAnchorBounds$delegate, it);
        return Unit.INSTANCE;
    }

    private static final Unit GroupContent$lambda$259$lambda$258$lambda$257$lambda$188$lambda$151$lambda$150$lambda$149(float $groupCollapsedTitleAlpha, GraphicsLayerScope $this$graphicsLayer) {
        Intrinsics.checkNotNullParameter((Object)$this$graphicsLayer, (String)"$this$graphicsLayer");
        $this$graphicsLayer.setAlpha($groupCollapsedTitleAlpha);
        return Unit.INSTANCE;
    }

    private static final Unit GroupContent$lambda$259$lambda$258$lambda$257$lambda$188$lambda$153$lambda$152(float $groupExpandedTitleAlpha, float $groupExpandedTitleTranslateY, GraphicsLayerScope $this$graphicsLayer) {
        Intrinsics.checkNotNullParameter((Object)$this$graphicsLayer, (String)"$this$graphicsLayer");
        $this$graphicsLayer.setAlpha($groupExpandedTitleAlpha);
        $this$graphicsLayer.setTranslationY($groupExpandedTitleTranslateY);
        return Unit.INSTANCE;
    }

    /*
     * WARNING - void declaration
     */
    private static final Unit GroupContent$lambda$259$lambda$258$lambda$257$lambda$188$lambda$162$lambda$161(List $albumRows, int $albumColumns, GroupActivity this$0, NoMemoAdaptiveSpec $spec, Map $albumPreviewRecordsMap, Function1 $onOpenAlbumDetail, MutableState $groupListMoreExpanded$delegate, LazyListScope $this$LazyColumn) {
        void $this$items_u24default\1;
        Intrinsics.checkNotNullParameter((Object)$this$LazyColumn, (String)"$this$LazyColumn");
        LazyListScope lazyListScope = $this$LazyColumn;
        List list = $albumRows;
        Object key\1 = null;
        Function1 function1 = GroupContent$lambda$259$lambda$258$lambda$257$lambda$188$lambda$162$lambda$161$$inlined$items$default$1.INSTANCE;
        boolean bl = false;
        $this$items_u24default\1.items(list.size(), null, (Function1)new Function1<Integer, Object>(function1, list){
            final /* synthetic */ Function1 $contentType;
            final /* synthetic */ List $items;
            {
                this.$contentType = $contentType;
                this.$items = $items;
            }

            public final Object invoke(int index) {
                return this.$contentType.invoke(this.$items.get(index));
            }
        }, (Function4)ComposableLambdaKt.composableLambdaInstance((int)802480018, (boolean)true, (Object)new Function4<LazyItemScope, Integer, Composer, Integer, Unit>(list, $albumColumns, this$0, $spec, $albumPreviewRecordsMap, $onOpenAlbumDetail, $groupListMoreExpanded$delegate){
            final /* synthetic */ List $items;
            final /* synthetic */ int $albumColumns$inlined;
            final /* synthetic */ GroupActivity this$0;
            final /* synthetic */ NoMemoAdaptiveSpec $spec$inlined;
            final /* synthetic */ Map $albumPreviewRecordsMap$inlined;
            final /* synthetic */ Function1 $onOpenAlbumDetail$inlined;
            final /* synthetic */ MutableState $groupListMoreExpanded$delegate$inlined;
            {
                this.$items = $items;
                this.$albumColumns$inlined = n;
                this.this$0 = groupActivity;
                this.$spec$inlined = noMemoAdaptiveSpec;
                this.$albumPreviewRecordsMap$inlined = map;
                this.$onOpenAlbumDetail$inlined = function1;
                this.$groupListMoreExpanded$delegate$inlined = mutableState;
            }

            /*
             * WARNING - void declaration
             */
            @Composable
            public final void invoke(LazyItemScope $this$items, int it, Composer $composer, int $changed) {
                ComposerKt.sourceInformation((Composer)$composer, (String)"CN(it)178@8834L22:LazyDsl.kt#428nma");
                int $dirty = $changed;
                if (($changed & 6) == 0) {
                    $dirty |= $composer.changed((Object)$this$items) ? 4 : 2;
                }
                if (($changed & 0x30) == 0) {
                    $dirty |= $composer.changed(it) ? 32 : 16;
                }
                if ($composer.shouldExecute(($dirty & 0x93) != 146, $dirty & 1)) {
                    void rowAlbums\1;
                    void $composer\10;
                    void $changed\5;
                    void $changed\4;
                    void modifier\4;
                    void modifier\3;
                    void $changed\3;
                    void horizontalArrangement\3;
                    void $composer\3;
                    void $composer\1;
                    if (ComposerKt.isTraceInProgress()) {
                        ComposerKt.traceEventStart((int)802480018, (int)$dirty, (int)-1, (String)"androidx.compose.foundation.lazy.items.<anonymous> (LazyDsl.kt:178)");
                    }
                    int n = 0xE & $dirty;
                    Composer composer = $composer;
                    List list = (List)this.$items.get(it);
                    LazyItemScope lazyItemScope = $this$items;
                    boolean bl = false;
                    $composer\1.startReplaceGroup(-783000376);
                    ComposerKt.sourceInformation((Composer)$composer\1, (String)"CN(rowAlbums)*609@27522L1477:GroupActivity.kt#83vr7l");
                    Modifier modifier = SizeKt.fillMaxWidth$default((Modifier)((Modifier)Modifier.Companion), (float)0.0f, (int)1, null);
                    int $this$dp\32 = 12;
                    boolean bl2 = false;
                    Arrangement.Horizontal $this$dp\32 = (Arrangement.Horizontal)Arrangement.INSTANCE.spacedBy-0680j_4(Dp.constructor-impl((float)$this$dp\32));
                    void var14_16 = $composer\1;
                    int n2 = 54;
                    boolean bl3 = false;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer\3, (int)844473419, (String)"CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo");
                    Alignment.Vertical vertical = Alignment.Companion.getTop();
                    MeasurePolicy measurePolicy = RowKt.rowMeasurePolicy((Arrangement.Horizontal)horizontalArrangement\3, (Alignment.Vertical)vertical, (Composer)$composer\3, (int)(0xE & $changed\3 >> 3 | 0x70 & $changed\3 >> 3));
                    void var18_20 = modifier\3;
                    int n3 = 0x70 & $changed\3 << 3;
                    boolean bl4 = false;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer\3, (int)-1159599143, (String)"CC(Layout)P(!1,2)80@3267L27,83@3433L360:Layout.kt#80mrfh");
                    int n4 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode((Composer)$composer\3, (int)0));
                    CompositionLocalMap compositionLocalMap = $composer\3.getCurrentCompositionLocalMap();
                    Modifier modifier2 = ComposedModifierKt.materializeModifier((Composer)$composer\3, (Modifier)modifier\4);
                    Function0 function0 = ComposeUiNode.Companion.getConstructor();
                    int n5 = 6 | 0x380 & $changed\4 << 6;
                    boolean bl5 = false;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer\3, (int)-553112988, (String)"CC(ReusableComposeNode)N(factory,update,content)399@15590L9:Composables.kt#9igjgp");
                    if (!($composer\3.getApplier() instanceof Applier)) {
                        ComposablesKt.invalidApplier();
                    }
                    $composer\3.startReusableNode();
                    if ($composer\3.getInserting()) {
                        void factory\5;
                        $composer\3.createNode((Function0)factory\5);
                    } else {
                        $composer\3.useNode();
                    }
                    Composer composer2 = Updater.constructor-impl((Composer)$composer\3);
                    boolean bl6 = false;
                    Updater.set-impl((Composer)composer2, (Object)measurePolicy, (Function2)ComposeUiNode.Companion.getSetMeasurePolicy());
                    Updater.set-impl((Composer)composer2, (Object)compositionLocalMap, (Function2)ComposeUiNode.Companion.getSetResolvedCompositionLocals());
                    Function2 function2 = ComposeUiNode.Companion.getSetCompositeKeyHash();
                    boolean bl7 = false;
                    Composer composer3 = composer2;
                    boolean bl8 = false;
                    if (composer3.getInserting() || !Intrinsics.areEqual((Object)composer3.rememberedValue(), (Object)n4)) {
                        composer3.updateRememberedValue((Object)n4);
                        composer2.apply((Object)n4, function2);
                    }
                    Updater.set-impl((Composer)composer2, (Object)modifier2, (Function2)ComposeUiNode.Companion.getSetModifier());
                    int n6 = 0xE & $changed\5 >> 6;
                    void $composer\9 = $composer\3;
                    boolean bl9 = false;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer\9, (int)1456264949, (String)"C101@5233L9:Row.kt#2w3rfo");
                    int n7 = 6 | 0x70 & $changed\3 >> 6;
                    void var37_39 = $composer\9;
                    RowScope rowScope = (RowScope)RowScopeInstance.INSTANCE;
                    boolean bl10 = false;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer\10, (int)-881698469, (String)"C:GroupActivity.kt#83vr7l");
                    $composer\10.startReplaceGroup(-1968104443);
                    ComposerKt.sourceInformation((Composer)$composer\10, (String)"*620@28414L230,614@27868L826");
                    Iterable iterable = (Iterable)rowAlbums\1;
                    boolean bl11 = false;
                    for (T t : iterable) {
                        Object object;
                        void $this$cache\13;
                        GroupAlbumStore.GroupAlbum groupAlbum = (GroupAlbumStore.GroupAlbum)t;
                        boolean bl12 = false;
                        GroupActivity groupActivity = this.this$0;
                        GroupAlbumStore.GroupAlbum groupAlbum2 = groupAlbum;
                        boolean bl13 = this.$spec$inlined.getWidthClass() == NoMemoWidthClass.COMPACT;
                        int n8 = groupAlbum.getRecordIds().size();
                        List list2 = (List)this.$albumPreviewRecordsMap$inlined.get(groupAlbum.getAlbumId());
                        if (list2 == null) {
                            list2 = CollectionsKt.emptyList();
                        }
                        Modifier modifier3 = RowScope.weight$default((RowScope)rowScope, (Modifier)((Modifier)Modifier.Companion), (float)1.0f, (boolean)false, (int)2, null);
                        ComposerKt.sourceInformationMarkerStart((Composer)$composer\10, (int)-1613978097, (String)"CC(remember):GroupActivity.kt#9igjgp");
                        void var46_51 = $composer\10;
                        boolean bl14 = $composer\10.changed((Object)this.$onOpenAlbumDetail$inlined) | $composer\10.changedInstance((Object)groupAlbum);
                        boolean bl15 = false;
                        Object object2 = $this$cache\13.rememberedValue();
                        boolean bl16 = false;
                        if (bl14 || object2 == Composer.Companion.getEmpty()) {
                            Modifier modifier4 = modifier3;
                            List list3 = list2;
                            int n9 = n8;
                            boolean bl17 = bl13;
                            GroupAlbumStore.GroupAlbum groupAlbum3 = groupAlbum2;
                            GroupActivity groupActivity2 = groupActivity;
                            boolean bl18 = false;
                            Function0 function02 = (Function0)new Function0<Unit>((Function1<? super String, Unit>)this.$onOpenAlbumDetail$inlined, groupAlbum, (MutableState<Boolean>)this.$groupListMoreExpanded$delegate$inlined){
                                final /* synthetic */ Function1<String, Unit> $onOpenAlbumDetail;
                                final /* synthetic */ GroupAlbumStore.GroupAlbum $album;
                                final /* synthetic */ MutableState<Boolean> $groupListMoreExpanded$delegate;
                                {
                                    this.$onOpenAlbumDetail = $onOpenAlbumDetail;
                                    this.$album = $album;
                                    this.$groupListMoreExpanded$delegate = $groupListMoreExpanded$delegate;
                                }

                                public final void invoke() {
                                    GroupActivity.access$GroupContent$lambda$53(this.$groupListMoreExpanded$delegate, false);
                                    this.$onOpenAlbumDetail.invoke((Object)this.$album.getAlbumId());
                                }
                            };
                            groupActivity = groupActivity2;
                            groupAlbum2 = groupAlbum3;
                            bl13 = bl17;
                            n8 = n9;
                            list2 = list3;
                            modifier3 = modifier4;
                            Function0 function03 = function02;
                            $this$cache\13.updateRememberedValue((Object)function03);
                            object = function03;
                        } else {
                            object = object2;
                        }
                        Function0 function04 = (Function0)object;
                        ComposerKt.sourceInformationMarkerEnd((Composer)$composer\10);
                        GroupActivity.access$GroupAlbumGridCard(groupActivity, groupAlbum2, bl13, n8, list2, modifier3, function04, (Composer)$composer\10, GroupAlbumStore.GroupAlbum.$stable, 0);
                    }
                    $composer\10.endReplaceGroup();
                    $composer\10.startReplaceGroup(-1968073722);
                    ComposerKt.sourceInformation((Composer)$composer\10, (String)"*627@28873L38");
                    int n10 = this.$albumColumns$inlined - rowAlbums\1.size();
                    int n11 = 0;
                    while (n11 < n10) {
                        int n12 = n11++;
                        boolean bl19 = false;
                        SpacerKt.Spacer((Modifier)RowScope.weight$default((RowScope)rowScope, (Modifier)((Modifier)Modifier.Companion), (float)1.0f, (boolean)false, (int)2, null), (Composer)$composer\10, (int)0);
                    }
                    $composer\10.endReplaceGroup();
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer\10);
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer\9);
                    $composer\3.endNode();
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer\3);
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer\3);
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer\3);
                    $composer\1.endReplaceGroup();
                    if (ComposerKt.isTraceInProgress()) {
                        ComposerKt.traceEventEnd();
                    }
                } else {
                    $composer.skipToGroupEnd();
                }
            }
        }));
        return Unit.INSTANCE;
    }

    private static final Unit GroupContent$lambda$259$lambda$258$lambda$257$lambda$188$lambda$168$lambda$164$lambda$163(MutableState $albumSelectionModeActive$delegate, MutableState $selectedAlbumRecordIds$delegate, MutableState $showRemoveFromAlbumConfirm$delegate, MutableState $showDeleteSelectedConfirm$delegate) {
        GroupActivity.GroupContent$lambda$68((MutableState<Boolean>)$albumSelectionModeActive$delegate, false);
        GroupActivity.GroupContent$lambda$65((MutableState<Set<String>>)$selectedAlbumRecordIds$delegate, SetsKt.emptySet());
        GroupActivity.GroupContent$lambda$71((MutableState<Boolean>)$showRemoveFromAlbumConfirm$delegate, false);
        GroupActivity.GroupContent$lambda$74((MutableState<Boolean>)$showDeleteSelectedConfirm$delegate, false);
        return Unit.INSTANCE;
    }

    /*
     * WARNING - void declaration
     */
    private static final Unit GroupContent$lambda$259$lambda$258$lambda$257$lambda$188$lambda$168$lambda$167$lambda$166(boolean $allOpenedRecordsSelected, List $openedRecords, MutableState $selectedAlbumRecordIds$delegate, MutableState $showRemoveFromAlbumConfirm$delegate, MutableState $showDeleteSelectedConfirm$delegate) {
        Set set;
        MutableState mutableState = $selectedAlbumRecordIds$delegate;
        if ($allOpenedRecordsSelected) {
            set = SetsKt.emptySet();
        } else {
            void $this$mapTo\2;
            void $this$map\1;
            Iterable iterable = $openedRecords;
            MutableState mutableState2 = mutableState;
            boolean bl = false;
            void var7_8 = $this$map\1;
            Collection collection = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map\1, (int)10));
            boolean bl2 = false;
            for (Object t : $this$mapTo\2) {
                void it\3;
                MemoryRecord memoryRecord = (MemoryRecord)t;
                Collection collection2 = collection;
                boolean bl3 = false;
                collection2.add(it\3.getRecordId());
            }
            mutableState = mutableState2;
            set = CollectionsKt.toSet((Iterable)((List)collection));
        }
        GroupActivity.GroupContent$lambda$65((MutableState<Set<String>>)mutableState, set);
        GroupActivity.GroupContent$lambda$71((MutableState<Boolean>)$showRemoveFromAlbumConfirm$delegate, false);
        GroupActivity.GroupContent$lambda$74((MutableState<Boolean>)$showDeleteSelectedConfirm$delegate, false);
        return Unit.INSTANCE;
    }

    private static final Unit GroupContent$lambda$259$lambda$258$lambda$257$lambda$188$lambda$176$lambda$175$lambda$170$lambda$169(boolean $openedAsStandaloneDetail, Function0 $onCloseAlbumDetail, MutableState $openedAlbumId$delegate) {
        if ($openedAsStandaloneDetail) {
            $onCloseAlbumDetail.invoke();
        } else {
            GroupActivity.GroupContent$lambda$38((MutableState<String>)$openedAlbumId$delegate, null);
        }
        return Unit.INSTANCE;
    }

    private static final Unit GroupContent$lambda$259$lambda$258$lambda$257$lambda$188$lambda$176$lambda$175$lambda$172$lambda$171(MutableState $detailMoreExpanded$delegate) {
        GroupActivity.GroupContent$lambda$56((MutableState<Boolean>)$detailMoreExpanded$delegate, !GroupActivity.GroupContent$lambda$55((MutableState<Boolean>)$detailMoreExpanded$delegate));
        return Unit.INSTANCE;
    }

    private static final Unit GroupContent$lambda$259$lambda$258$lambda$257$lambda$188$lambda$176$lambda$175$lambda$174$lambda$173(MutableState $detailMoreAnchorBounds$delegate, IntRect it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        GroupActivity.GroupContent$lambda$62((MutableState<IntRect>)$detailMoreAnchorBounds$delegate, it);
        return Unit.INSTANCE;
    }

    private static final Object GroupContent$lambda$259$lambda$258$lambda$257$lambda$188$lambda$182$lambda$181$lambda$177(MemoryRecord it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        String string2 = it.getRecordId();
        Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"getRecordId(...)");
        return string2;
    }

    /*
     * WARNING - void declaration
     */
    private static final Unit GroupContent$lambda$259$lambda$258$lambda$257$lambda$188$lambda$182$lambda$181(List $openedRecords, NoMemoPalette $albumPalette, Function1 $onOpenDetail, NoMemoAdaptiveSpec $albumAdaptive, MutableState $selectedAlbumRecordIds$delegate, MutableState $albumSelectionModeActive$delegate, MutableState $showRemoveFromAlbumConfirm$delegate, MutableState $showDeleteSelectedConfirm$delegate, MutableState $detailMoreExpanded$delegate, LazyListScope $this$LazyColumn) {
        void items\1;
        void $this$items_u24default\1;
        Intrinsics.checkNotNullParameter((Object)$this$LazyColumn, (String)"$this$LazyColumn");
        LazyListScope lazyListScope = $this$LazyColumn;
        List list = $openedRecords;
        Function1 function1 = GroupActivity::GroupContent$lambda$259$lambda$258$lambda$257$lambda$188$lambda$182$lambda$181$lambda$177;
        Function1 function12 = GroupContent$lambda$259$lambda$258$lambda$257$lambda$188$lambda$182$lambda$181$$inlined$items$default$1.INSTANCE;
        boolean bl = false;
        $this$items_u24default\1.items(items\1.size(), function1 != null ? (Function1)new Function1<Integer, Object>(function1, (List)items\1){
            final /* synthetic */ Function1 $key;
            final /* synthetic */ List $items;
            {
                this.$key = $key;
                this.$items = $items;
            }

            public final Object invoke(int index) {
                return this.$key.invoke(this.$items.get(index));
            }
        } : null, (Function1)new Function1<Integer, Object>(function12, (List)items\1){
            final /* synthetic */ Function1 $contentType;
            final /* synthetic */ List $items;
            {
                this.$contentType = $contentType;
                this.$items = $items;
            }

            public final Object invoke(int index) {
                return this.$contentType.invoke(this.$items.get(index));
            }
        }, (Function4)ComposableLambdaKt.composableLambdaInstance((int)802480018, (boolean)true, (Object)new Function4<LazyItemScope, Integer, Composer, Integer, Unit>((List)items\1, $albumPalette, $onOpenDetail, $albumAdaptive, $selectedAlbumRecordIds$delegate, $albumSelectionModeActive$delegate, $showRemoveFromAlbumConfirm$delegate, $showDeleteSelectedConfirm$delegate, $detailMoreExpanded$delegate){
            final /* synthetic */ List $items;
            final /* synthetic */ NoMemoPalette $albumPalette$inlined;
            final /* synthetic */ Function1 $onOpenDetail$inlined;
            final /* synthetic */ NoMemoAdaptiveSpec $albumAdaptive$inlined;
            final /* synthetic */ MutableState $selectedAlbumRecordIds$delegate$inlined;
            final /* synthetic */ MutableState $albumSelectionModeActive$delegate$inlined;
            final /* synthetic */ MutableState $showRemoveFromAlbumConfirm$delegate$inlined;
            final /* synthetic */ MutableState $showDeleteSelectedConfirm$delegate$inlined;
            final /* synthetic */ MutableState $detailMoreExpanded$delegate$inlined;
            {
                this.$items = $items;
                this.$albumPalette$inlined = noMemoPalette;
                this.$onOpenDetail$inlined = function1;
                this.$albumAdaptive$inlined = noMemoAdaptiveSpec;
                this.$selectedAlbumRecordIds$delegate$inlined = mutableState;
                this.$albumSelectionModeActive$delegate$inlined = mutableState2;
                this.$showRemoveFromAlbumConfirm$delegate$inlined = mutableState3;
                this.$showDeleteSelectedConfirm$delegate$inlined = mutableState4;
                this.$detailMoreExpanded$delegate$inlined = mutableState5;
            }

            /*
             * WARNING - void declaration
             */
            @Composable
            public final void invoke(LazyItemScope $this$items, int it, Composer $composer, int $changed) {
                ComposerKt.sourceInformation((Composer)$composer, (String)"CN(it)178@8834L22:LazyDsl.kt#428nma");
                int $dirty = $changed;
                if (($changed & 6) == 0) {
                    $dirty |= $composer.changed((Object)$this$items) ? 4 : 2;
                }
                if (($changed & 0x30) == 0) {
                    $dirty |= $composer.changed(it) ? 32 : 16;
                }
                if ($composer.shouldExecute(($dirty & 0x93) != 146, $dirty & 1)) {
                    void $changed\1;
                    Object object;
                    void $this$cache\5;
                    Object object2;
                    Function0 function0;
                    void var21_20;
                    Modifier modifier;
                    boolean bl;
                    void $this$cache\2;
                    void record\1;
                    void $composer\1;
                    if (ComposerKt.isTraceInProgress()) {
                        ComposerKt.traceEventStart((int)802480018, (int)$dirty, (int)-1, (String)"androidx.compose.foundation.lazy.items.<anonymous> (LazyDsl.kt:178)");
                    }
                    int n = 0xE & $dirty;
                    Composer composer = $composer;
                    MemoryRecord memoryRecord = (MemoryRecord)this.$items.get(it);
                    LazyItemScope lazyItemScope = $this$items;
                    boolean bl2 = false;
                    $composer\1.startReplaceGroup(789104858);
                    ComposerKt.sourceInformation((Composer)$composer\1, (String)"CN(record)*757@37216L882,770@38158L749,746@36463L2486:GroupActivity.kt#83vr7l");
                    boolean bl3 = GroupActivity.access$GroupContent$lambda$64(this.$selectedAlbumRecordIds$delegate$inlined).contains(record\1.getRecordId());
                    long l = ComposeUiKt.noMemoCardSurfaceColor-4WTKRHQ(true, Color.copy-wmQWz5c$default((long)this.$albumPalette$inlined.getGlassFill-0d7_KjU(), (float)0.92f, (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null));
                    void v0 = record\1;
                    Modifier modifier2 = null;
                    boolean bl4 = bl3;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer\1, (int)1826596029, (String)"CC(remember):GroupActivity.kt#9igjgp");
                    void var14_13 = $composer\1;
                    boolean bl5 = $composer\1.changed(bl3) | $composer\1.changedInstance((Object)record\1) | $composer\1.changed((Object)this.$onOpenDetail$inlined);
                    boolean bl6 = false;
                    Object object3 = $this$cache\2.rememberedValue();
                    boolean bl7 = false;
                    if (bl5 || object3 == Composer.Companion.getEmpty()) {
                        bl = bl4;
                        modifier = modifier2;
                        var21_20 = v0;
                        boolean bl8 = false;
                        function0 = (Function0)new Function0<Unit>(bl3, (MemoryRecord)record\1, (Function1<? super MemoryRecord, Unit>)this.$onOpenDetail$inlined, (MutableState<Boolean>)this.$albumSelectionModeActive$delegate$inlined, (MutableState<Set<String>>)this.$selectedAlbumRecordIds$delegate$inlined, (MutableState<Boolean>)this.$showRemoveFromAlbumConfirm$delegate$inlined, (MutableState<Boolean>)this.$showDeleteSelectedConfirm$delegate$inlined){
                            final /* synthetic */ boolean $selected;
                            final /* synthetic */ MemoryRecord $record;
                            final /* synthetic */ Function1<MemoryRecord, Unit> $onOpenDetail;
                            final /* synthetic */ MutableState<Boolean> $albumSelectionModeActive$delegate;
                            final /* synthetic */ MutableState<Set<String>> $selectedAlbumRecordIds$delegate;
                            final /* synthetic */ MutableState<Boolean> $showRemoveFromAlbumConfirm$delegate;
                            final /* synthetic */ MutableState<Boolean> $showDeleteSelectedConfirm$delegate;
                            {
                                this.$selected = $selected;
                                this.$record = $record;
                                this.$onOpenDetail = $onOpenDetail;
                                this.$albumSelectionModeActive$delegate = $albumSelectionModeActive$delegate;
                                this.$selectedAlbumRecordIds$delegate = $selectedAlbumRecordIds$delegate;
                                this.$showRemoveFromAlbumConfirm$delegate = $showRemoveFromAlbumConfirm$delegate;
                                this.$showDeleteSelectedConfirm$delegate = $showDeleteSelectedConfirm$delegate;
                            }

                            public final void invoke() {
                                if (GroupActivity.access$GroupContent$lambda$67(this.$albumSelectionModeActive$delegate)) {
                                    GroupActivity.access$GroupContent$lambda$65(this.$selectedAlbumRecordIds$delegate, this.$selected ? SetsKt.minus((Set)GroupActivity.access$GroupContent$lambda$64(this.$selectedAlbumRecordIds$delegate), (Object)this.$record.getRecordId()) : SetsKt.plus((Set)GroupActivity.access$GroupContent$lambda$64(this.$selectedAlbumRecordIds$delegate), (Object)this.$record.getRecordId()));
                                    GroupActivity.access$GroupContent$lambda$71(this.$showRemoveFromAlbumConfirm$delegate, false);
                                    GroupActivity.access$GroupContent$lambda$74(this.$showDeleteSelectedConfirm$delegate, false);
                                } else {
                                    this.$onOpenDetail.invoke((Object)this.$record);
                                }
                            }
                        };
                        v0 = var21_20;
                        modifier2 = modifier;
                        bl4 = bl;
                        Function0 function02 = function0;
                        $this$cache\2.updateRememberedValue((Object)function02);
                        object2 = function02;
                    } else {
                        object2 = object3;
                    }
                    Function0 function03 = (Function0)object2;
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer\1);
                    Function0 function04 = function03;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer\1, (int)1826626040, (String)"CC(remember):GroupActivity.kt#9igjgp");
                    $this$cache\2 = $composer\1;
                    boolean bl9 = $composer\1.changed(bl3) | $composer\1.changedInstance((Object)record\1);
                    boolean bl10 = false;
                    Object object4 = $this$cache\5.rememberedValue();
                    boolean bl11 = false;
                    if (bl9 || object4 == Composer.Companion.getEmpty()) {
                        function0 = function04;
                        bl = bl4;
                        modifier = modifier2;
                        var21_20 = v0;
                        boolean bl12 = false;
                        Function0 function05 = (Function0)new Function0<Unit>(bl3, (MemoryRecord)record\1, (MutableState<Boolean>)this.$detailMoreExpanded$delegate$inlined, (MutableState<Boolean>)this.$albumSelectionModeActive$delegate$inlined, (MutableState<Set<String>>)this.$selectedAlbumRecordIds$delegate$inlined, (MutableState<Boolean>)this.$showRemoveFromAlbumConfirm$delegate$inlined, (MutableState<Boolean>)this.$showDeleteSelectedConfirm$delegate$inlined){
                            final /* synthetic */ boolean $selected;
                            final /* synthetic */ MemoryRecord $record;
                            final /* synthetic */ MutableState<Boolean> $detailMoreExpanded$delegate;
                            final /* synthetic */ MutableState<Boolean> $albumSelectionModeActive$delegate;
                            final /* synthetic */ MutableState<Set<String>> $selectedAlbumRecordIds$delegate;
                            final /* synthetic */ MutableState<Boolean> $showRemoveFromAlbumConfirm$delegate;
                            final /* synthetic */ MutableState<Boolean> $showDeleteSelectedConfirm$delegate;
                            {
                                this.$selected = $selected;
                                this.$record = $record;
                                this.$detailMoreExpanded$delegate = $detailMoreExpanded$delegate;
                                this.$albumSelectionModeActive$delegate = $albumSelectionModeActive$delegate;
                                this.$selectedAlbumRecordIds$delegate = $selectedAlbumRecordIds$delegate;
                                this.$showRemoveFromAlbumConfirm$delegate = $showRemoveFromAlbumConfirm$delegate;
                                this.$showDeleteSelectedConfirm$delegate = $showDeleteSelectedConfirm$delegate;
                            }

                            public final void invoke() {
                                GroupActivity.access$GroupContent$lambda$56(this.$detailMoreExpanded$delegate, false);
                                GroupActivity.access$GroupContent$lambda$68(this.$albumSelectionModeActive$delegate, true);
                                GroupActivity.access$GroupContent$lambda$65(this.$selectedAlbumRecordIds$delegate, this.$selected ? SetsKt.minus((Set)GroupActivity.access$GroupContent$lambda$64(this.$selectedAlbumRecordIds$delegate), (Object)this.$record.getRecordId()) : SetsKt.plus((Set)GroupActivity.access$GroupContent$lambda$64(this.$selectedAlbumRecordIds$delegate), (Object)this.$record.getRecordId()));
                                GroupActivity.access$GroupContent$lambda$71(this.$showRemoveFromAlbumConfirm$delegate, false);
                                GroupActivity.access$GroupContent$lambda$74(this.$showDeleteSelectedConfirm$delegate, false);
                            }
                        };
                        v0 = var21_20;
                        modifier2 = modifier;
                        bl4 = bl;
                        function04 = function0;
                        Function0 function06 = function05;
                        $this$cache\5.updateRememberedValue((Object)function06);
                        object = function06;
                    } else {
                        object = object4;
                    }
                    function03 = (Function0)object;
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer\1);
                    ComposeUiKt.RecordCard-IJOc0f0((MemoryRecord)v0, modifier2, bl4, (Function0<Unit>)function04, (Function0<Unit>)function03, this.$albumPalette$inlined, this.$albumAdaptive$inlined, true, false, Color.box-impl((long)l), null, (Composer)$composer\1, 0x6C00000 | 0xE & $changed\1 >> 3, 0, 1026);
                    $composer\1.endReplaceGroup();
                    if (ComposerKt.isTraceInProgress()) {
                        ComposerKt.traceEventEnd();
                    }
                } else {
                    $composer.skipToGroupEnd();
                }
            }
        }));
        return Unit.INSTANCE;
    }

    private static final Unit GroupContent$lambda$259$lambda$258$lambda$257$lambda$188$lambda$187$lambda$184$lambda$183(MutableState $selectedExistingRecordIds$delegate, MutableState $addExistingSearchQuery$delegate, MutableState $showAddExistingSheet$delegate) {
        GroupActivity.GroupContent$lambda$47((MutableState<Set<String>>)$selectedExistingRecordIds$delegate, SetsKt.emptySet());
        GroupActivity.GroupContent$lambda$50((MutableState<String>)$addExistingSearchQuery$delegate, "");
        GroupActivity.GroupContent$lambda$44((MutableState<Boolean>)$showAddExistingSheet$delegate, true);
        return Unit.INSTANCE;
    }

    private static final Unit GroupContent$lambda$259$lambda$258$lambda$257$lambda$188$lambda$187$lambda$186$lambda$185(GroupAlbumStore.GroupAlbum $openedAlbum, Context $albumContext, SettingsStore $settingsStore, GroupAlbumStore $albumStore, MutableState $albumList$delegate) {
        GroupAlbumStore.GroupAlbum groupAlbum = $openedAlbum;
        if (groupAlbum == null) {
            return Unit.INSTANCE;
        }
        GroupAlbumStore.GroupAlbum currentAlbum = groupAlbum;
        if (Intrinsics.areEqual((Object)currentAlbum.getOrganizeStatus(), (Object)"processing")) {
            return Unit.INSTANCE;
        }
        if (StringsKt.isBlank((CharSequence)currentAlbum.getDescription())) {
            Toast.makeText((Context)$albumContext, (CharSequence)"\u8bf7\u5148\u586b\u5199\u5206\u7ec4\u63cf\u8ff0", (int)0).show();
            return Unit.INSTANCE;
        }
        if (!$settingsStore.isAiAvailable()) {
            Toast.makeText((Context)$albumContext, (CharSequence)"\u8bf7\u5148\u5b8c\u6210 AI \u914d\u7f6e\u540e\u518d\u4f7f\u7528\u6574\u7406\u5386\u53f2\u8bb0\u5fc6", (int)0).show();
            return Unit.INSTANCE;
        }
        if ($albumStore.updateOrganizeStatus(currentAlbum.getAlbumId(), "processing")) {
            GroupActivity.GroupContent$lambda$35((MutableState<List<GroupAlbumStore.GroupAlbum>>)$albumList$delegate, $albumStore.loadAlbums());
        }
        GroupAiOrganizeWorkScheduler.enqueue($albumContext, currentAlbum.getAlbumId());
        return Unit.INSTANCE;
    }

    private static final Unit GroupContent$lambda$259$lambda$258$lambda$257$lambda$190$lambda$189() {
        return Unit.INSTANCE;
    }

    private static final Unit GroupContent$lambda$259$lambda$258$lambda$257$lambda$192$lambda$191(MutableState $showRemoveFromAlbumConfirm$delegate) {
        GroupActivity.GroupContent$lambda$71((MutableState<Boolean>)$showRemoveFromAlbumConfirm$delegate, true);
        return Unit.INSTANCE;
    }

    private static final Unit GroupContent$lambda$259$lambda$258$lambda$257$lambda$194$lambda$193(MutableState $showDeleteSelectedConfirm$delegate) {
        GroupActivity.GroupContent$lambda$74((MutableState<Boolean>)$showDeleteSelectedConfirm$delegate, true);
        return Unit.INSTANCE;
    }

    private static final Unit GroupContent$lambda$259$lambda$258$lambda$257$lambda$196$lambda$195(MutableState $groupListMoreExpanded$delegate) {
        GroupActivity.GroupContent$lambda$53((MutableState<Boolean>)$groupListMoreExpanded$delegate, false);
        return Unit.INSTANCE;
    }

    private static final Unit GroupContent$lambda$259$lambda$258$lambda$257$lambda$198$lambda$197(MutableState $groupListMoreExpanded$delegate, MutableState $showCreateAlbumDialog$delegate) {
        GroupActivity.GroupContent$lambda$53((MutableState<Boolean>)$groupListMoreExpanded$delegate, false);
        GroupActivity.GroupContent$lambda$41((MutableState<Boolean>)$showCreateAlbumDialog$delegate, true);
        return Unit.INSTANCE;
    }

    private static final Unit GroupContent$lambda$259$lambda$258$lambda$257$lambda$200$lambda$199(GroupActivity this$0, Context $albumContext, MutableState $groupListMoreExpanded$delegate) {
        GroupActivity.GroupContent$lambda$53((MutableState<Boolean>)$groupListMoreExpanded$delegate, false);
        this$0.startActivity(ArchivedMemoryActivity.Companion.createIntent($albumContext));
        return Unit.INSTANCE;
    }

    private static final Unit GroupContent$lambda$259$lambda$258$lambda$257$lambda$202$lambda$201(Function0 $onOpenSettings, MutableState $groupListMoreExpanded$delegate) {
        GroupActivity.GroupContent$lambda$53((MutableState<Boolean>)$groupListMoreExpanded$delegate, false);
        $onOpenSettings.invoke();
        return Unit.INSTANCE;
    }

    private static final Unit GroupContent$lambda$259$lambda$258$lambda$257$lambda$204$lambda$203(MutableState $detailMoreExpanded$delegate) {
        GroupActivity.GroupContent$lambda$56((MutableState<Boolean>)$detailMoreExpanded$delegate, false);
        return Unit.INSTANCE;
    }

    /*
     * WARNING - void declaration
     */
    private static final Unit GroupContent$lambda$259$lambda$258$lambda$257$lambda$207$lambda$206(List $openedRecords, MutableState $detailMoreExpanded$delegate, MutableState $albumSelectionModeActive$delegate, MutableState $selectedAlbumRecordIds$delegate, MutableState $showRemoveFromAlbumConfirm$delegate, MutableState $showDeleteSelectedConfirm$delegate) {
        void $this$mapTo\2;
        void $this$map\1;
        GroupActivity.GroupContent$lambda$56((MutableState<Boolean>)$detailMoreExpanded$delegate, false);
        GroupActivity.GroupContent$lambda$68((MutableState<Boolean>)$albumSelectionModeActive$delegate, true);
        Iterable iterable = $openedRecords;
        MutableState mutableState = $selectedAlbumRecordIds$delegate;
        boolean bl = false;
        void var8_9 = $this$map\1;
        Collection collection = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map\1, (int)10));
        boolean bl2 = false;
        for (Object t : $this$mapTo\2) {
            void it\3;
            MemoryRecord memoryRecord = (MemoryRecord)t;
            Collection collection2 = collection;
            boolean bl3 = false;
            collection2.add(it\3.getRecordId());
        }
        GroupActivity.GroupContent$lambda$65((MutableState<Set<String>>)mutableState, CollectionsKt.toSet((Iterable)((List)collection)));
        GroupActivity.GroupContent$lambda$71((MutableState<Boolean>)$showRemoveFromAlbumConfirm$delegate, false);
        GroupActivity.GroupContent$lambda$74((MutableState<Boolean>)$showDeleteSelectedConfirm$delegate, false);
        return Unit.INSTANCE;
    }

    private static final Unit GroupContent$lambda$259$lambda$258$lambda$257$lambda$209$lambda$208(MutableState $detailMoreExpanded$delegate, MutableState $selectedExistingRecordIds$delegate, MutableState $addExistingSearchQuery$delegate, MutableState $showAddExistingSheet$delegate) {
        GroupActivity.GroupContent$lambda$56((MutableState<Boolean>)$detailMoreExpanded$delegate, false);
        GroupActivity.GroupContent$lambda$47((MutableState<Set<String>>)$selectedExistingRecordIds$delegate, SetsKt.emptySet());
        GroupActivity.GroupContent$lambda$50((MutableState<String>)$addExistingSearchQuery$delegate, "");
        GroupActivity.GroupContent$lambda$44((MutableState<Boolean>)$showAddExistingSheet$delegate, true);
        return Unit.INSTANCE;
    }

    private static final Unit GroupContent$lambda$259$lambda$258$lambda$257$lambda$212$lambda$211(GroupAlbumStore.GroupAlbum $openedAlbum, MutableState $detailMoreExpanded$delegate, MutableState $editingAlbumId$delegate, MutableState $albumNameInput$delegate, MutableState $albumDescriptionInput$delegate, MutableState $showEditAlbumDialog$delegate) {
        block0: {
            GroupActivity.GroupContent$lambda$56((MutableState<Boolean>)$detailMoreExpanded$delegate, false);
            GroupAlbumStore.GroupAlbum groupAlbum = $openedAlbum;
            if (groupAlbum == null) break block0;
            GroupAlbumStore.GroupAlbum groupAlbum2 = groupAlbum;
            boolean bl = false;
            GroupActivity.GroupContent$lambda$86((MutableState<String>)$editingAlbumId$delegate, groupAlbum2.getAlbumId());
            GroupActivity.GroupContent$lambda$89((MutableState<String>)$albumNameInput$delegate, groupAlbum2.getName());
            GroupActivity.GroupContent$lambda$92((MutableState<String>)$albumDescriptionInput$delegate, groupAlbum2.getDescription());
            GroupActivity.GroupContent$lambda$77((MutableState<Boolean>)$showEditAlbumDialog$delegate, true);
        }
        return Unit.INSTANCE;
    }

    private static final Unit GroupContent$lambda$259$lambda$258$lambda$257$lambda$214$lambda$213(MutableState $detailMoreExpanded$delegate, MutableState $showDeleteAlbumConfirm$delegate) {
        GroupActivity.GroupContent$lambda$56((MutableState<Boolean>)$detailMoreExpanded$delegate, false);
        GroupActivity.GroupContent$lambda$80((MutableState<Boolean>)$showDeleteAlbumConfirm$delegate, true);
        return Unit.INSTANCE;
    }

    private static final Unit GroupContent$lambda$259$lambda$258$lambda$257$lambda$216$lambda$215(MutableState $albumAutoClassifyEnabledInput$delegate, boolean it) {
        GroupActivity.GroupContent$lambda$95((MutableState<Boolean>)$albumAutoClassifyEnabledInput$delegate, it);
        return Unit.INSTANCE;
    }

    private static final Unit GroupContent$lambda$259$lambda$258$lambda$257$lambda$218$lambda$217(MutableState $albumNameInput$delegate, String it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        GroupActivity.GroupContent$lambda$89((MutableState<String>)$albumNameInput$delegate, it);
        return Unit.INSTANCE;
    }

    private static final Unit GroupContent$lambda$259$lambda$258$lambda$257$lambda$220$lambda$219(MutableState $albumDescriptionInput$delegate, String it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        GroupActivity.GroupContent$lambda$92((MutableState<String>)$albumDescriptionInput$delegate, it);
        return Unit.INSTANCE;
    }

    private static final Unit GroupContent$lambda$259$lambda$258$lambda$257$lambda$222$lambda$221(MutableState $showCreateAlbumDialog$delegate, MutableState $albumNameInput$delegate, MutableState $albumDescriptionInput$delegate, MutableState $albumAutoClassifyEnabledInput$delegate) {
        GroupActivity.GroupContent$lambda$41((MutableState<Boolean>)$showCreateAlbumDialog$delegate, false);
        GroupActivity.GroupContent$lambda$89((MutableState<String>)$albumNameInput$delegate, "");
        GroupActivity.GroupContent$lambda$92((MutableState<String>)$albumDescriptionInput$delegate, "");
        GroupActivity.GroupContent$lambda$95((MutableState<Boolean>)$albumAutoClassifyEnabledInput$delegate, false);
        return Unit.INSTANCE;
    }

    private static final boolean GroupContent$lambda$259$lambda$258$lambda$257$lambda$225$lambda$224(Context $albumContext, SettingsStore $settingsStore, GroupAlbumStore $albumStore, MutableState $albumNameInput$delegate, MutableState $albumAutoClassifyEnabledInput$delegate, MutableState $albumDescriptionInput$delegate, MutableState $albumList$delegate) {
        boolean bl;
        String finalName;
        block8: {
            finalName = ((Object)StringsKt.trim((CharSequence)GroupActivity.GroupContent$lambda$88((MutableState<String>)$albumNameInput$delegate))).toString();
            if (StringsKt.isBlank((CharSequence)finalName)) {
                Toast.makeText((Context)$albumContext, (CharSequence)"\u8bf7\u8f93\u5165\u5206\u7ec4\u540d\u79f0", (int)0).show();
                return false;
            }
            if (GroupActivity.GroupContent$lambda$94((MutableState<Boolean>)$albumAutoClassifyEnabledInput$delegate) && StringsKt.isBlank((CharSequence)GroupActivity.GroupContent$lambda$91((MutableState<String>)$albumDescriptionInput$delegate))) {
                Toast.makeText((Context)$albumContext, (CharSequence)"\u8bf7\u5148\u586b\u5199\u5206\u7ec4\u63cf\u8ff0", (int)0).show();
                return false;
            }
            if (GroupActivity.GroupContent$lambda$94((MutableState<Boolean>)$albumAutoClassifyEnabledInput$delegate) && !$settingsStore.isAiAvailable()) {
                Toast.makeText((Context)$albumContext, (CharSequence)"\u8bf7\u5148\u5b8c\u6210 AI \u914d\u7f6e\u540e\u518d\u4f7f\u7528\u6574\u7406\u5386\u53f2\u8bb0\u5fc6", (int)0).show();
                return false;
            }
            String string2 = finalName;
            Locale locale = Locale.ROOT;
            Intrinsics.checkNotNullExpressionValue((Object)locale, (String)"ROOT");
            String string3 = string2.toLowerCase(locale);
            Intrinsics.checkNotNullExpressionValue((Object)string3, (String)"toLowerCase(...)");
            String normalized = string3;
            Iterable iterable = GroupActivity.GroupContent$lambda$34((MutableState<List<GroupAlbumStore.GroupAlbum>>)$albumList$delegate);
            boolean bl2 = false;
            if (iterable instanceof Collection && ((Collection)iterable).isEmpty()) {
                bl = false;
            } else {
                for (Object t : iterable) {
                    GroupAlbumStore.GroupAlbum groupAlbum = (GroupAlbumStore.GroupAlbum)t;
                    boolean bl3 = false;
                    String string4 = ((Object)StringsKt.trim((CharSequence)groupAlbum.getName())).toString();
                    Locale locale2 = Locale.ROOT;
                    Intrinsics.checkNotNullExpressionValue((Object)locale2, (String)"ROOT");
                    String string5 = string4.toLowerCase(locale2);
                    Intrinsics.checkNotNullExpressionValue((Object)string5, (String)"toLowerCase(...)");
                    if (!Intrinsics.areEqual((Object)string5, (Object)normalized)) continue;
                    bl = true;
                    break block8;
                }
                bl = false;
            }
        }
        if (bl) {
            Toast.makeText((Context)$albumContext, (CharSequence)"\u5df2\u5b58\u5728\u540c\u540d\u5206\u7ec4", (int)0).show();
            return false;
        }
        GroupAlbumStore.GroupAlbum createdAlbum = $albumStore.addAlbum(finalName, GroupActivity.GroupContent$lambda$91((MutableState<String>)$albumDescriptionInput$delegate), GroupActivity.GroupContent$lambda$94((MutableState<Boolean>)$albumAutoClassifyEnabledInput$delegate) ? "processing" : "idle");
        if (GroupActivity.GroupContent$lambda$94((MutableState<Boolean>)$albumAutoClassifyEnabledInput$delegate)) {
            GroupAiOrganizeWorkScheduler.enqueue($albumContext, createdAlbum.getAlbumId());
        }
        GroupActivity.GroupContent$lambda$35((MutableState<List<GroupAlbumStore.GroupAlbum>>)$albumList$delegate, $albumStore.loadAlbums());
        Toast.makeText((Context)$albumContext, (CharSequence)"\u5206\u7ec4\u5df2\u521b\u5efa", (int)0).show();
        return true;
    }

    private static final Unit GroupContent$lambda$259$lambda$258$lambda$257$lambda$227$lambda$226(MutableState $addExistingSearchQuery$delegate, String it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        GroupActivity.GroupContent$lambda$50((MutableState<String>)$addExistingSearchQuery$delegate, it);
        return Unit.INSTANCE;
    }

    private static final Unit GroupContent$lambda$259$lambda$258$lambda$257$lambda$229$lambda$228(MutableState $selectedExistingRecordIds$delegate, String recordId) {
        Intrinsics.checkNotNullParameter((Object)recordId, (String)"recordId");
        GroupActivity.GroupContent$lambda$47((MutableState<Set<String>>)$selectedExistingRecordIds$delegate, GroupActivity.GroupContent$lambda$46((MutableState<Set<String>>)$selectedExistingRecordIds$delegate).contains(recordId) ? SetsKt.minus(GroupActivity.GroupContent$lambda$46((MutableState<Set<String>>)$selectedExistingRecordIds$delegate), (Object)recordId) : SetsKt.plus(GroupActivity.GroupContent$lambda$46((MutableState<Set<String>>)$selectedExistingRecordIds$delegate), (Object)recordId));
        return Unit.INSTANCE;
    }

    private static final Unit GroupContent$lambda$259$lambda$258$lambda$257$lambda$231$lambda$230(MutableState $showAddExistingSheet$delegate, MutableState $selectedExistingRecordIds$delegate, MutableState $addExistingSearchQuery$delegate) {
        GroupActivity.GroupContent$lambda$44((MutableState<Boolean>)$showAddExistingSheet$delegate, false);
        GroupActivity.GroupContent$lambda$47((MutableState<Set<String>>)$selectedExistingRecordIds$delegate, SetsKt.emptySet());
        GroupActivity.GroupContent$lambda$50((MutableState<String>)$addExistingSearchQuery$delegate, "");
        return Unit.INSTANCE;
    }

    private static final boolean GroupContent$lambda$259$lambda$258$lambda$257$lambda$233$lambda$232(Context $albumContext, GroupAlbumStore $albumStore, MutableState $selectedExistingRecordIds$delegate, MutableState $openedAlbumId$delegate, MutableState $albumList$delegate) {
        if (GroupActivity.GroupContent$lambda$46((MutableState<Set<String>>)$selectedExistingRecordIds$delegate).isEmpty()) {
            Toast.makeText((Context)$albumContext, (CharSequence)"\u8bf7\u5148\u9009\u62e9\u8bb0\u5fc6", (int)0).show();
            return false;
        }
        String targetAlbumId = GroupActivity.GroupContent$lambda$37((MutableState<String>)$openedAlbumId$delegate);
        CharSequence charSequence = targetAlbumId;
        if (charSequence == null || StringsKt.isBlank((CharSequence)charSequence)) {
            Toast.makeText((Context)$albumContext, (CharSequence)"\u5206\u7ec4\u4e0d\u5b58\u5728\uff0c\u8bf7\u91cd\u8bd5", (int)0).show();
            return false;
        }
        boolean added = $albumStore.addRecordIds(targetAlbumId, (Collection<String>)GroupActivity.GroupContent$lambda$46((MutableState<Set<String>>)$selectedExistingRecordIds$delegate));
        GroupActivity.GroupContent$lambda$35((MutableState<List<GroupAlbumStore.GroupAlbum>>)$albumList$delegate, $albumStore.loadAlbums());
        if (!added) {
            Toast.makeText((Context)$albumContext, (CharSequence)"\u672a\u6dfb\u52a0\u6210\u529f\uff0c\u8bf7\u91cd\u8bd5", (int)0).show();
            return false;
        }
        Toast.makeText((Context)$albumContext, (CharSequence)"\u5df2\u6dfb\u52a0\u5230\u5206\u7ec4", (int)0).show();
        return true;
    }

    private static final Unit GroupContent$lambda$259$lambda$258$lambda$257$lambda$235$lambda$234(boolean it) {
        return Unit.INSTANCE;
    }

    private static final Unit GroupContent$lambda$259$lambda$258$lambda$257$lambda$237$lambda$236(MutableState $albumNameInput$delegate, String it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        GroupActivity.GroupContent$lambda$89((MutableState<String>)$albumNameInput$delegate, it);
        return Unit.INSTANCE;
    }

    private static final Unit GroupContent$lambda$259$lambda$258$lambda$257$lambda$239$lambda$238(MutableState $albumDescriptionInput$delegate, String it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        GroupActivity.GroupContent$lambda$92((MutableState<String>)$albumDescriptionInput$delegate, it);
        return Unit.INSTANCE;
    }

    private static final Unit GroupContent$lambda$259$lambda$258$lambda$257$lambda$241$lambda$240(MutableState $showEditAlbumDialog$delegate, MutableState $editingAlbumId$delegate) {
        GroupActivity.GroupContent$lambda$77((MutableState<Boolean>)$showEditAlbumDialog$delegate, false);
        GroupActivity.GroupContent$lambda$86((MutableState<String>)$editingAlbumId$delegate, null);
        return Unit.INSTANCE;
    }

    /*
     * Unable to fully structure code
     */
    private static final boolean GroupContent$lambda$259$lambda$258$lambda$257$lambda$244$lambda$243(GroupAlbumStore.GroupAlbum $openedAlbum, Context $albumContext, GroupAlbumStore $albumStore, MutableState $editingAlbumId$delegate, MutableState $albumNameInput$delegate, MutableState $albumList$delegate, MutableState $albumDescriptionInput$delegate) {
        block7: {
            block8: {
                v0 = GroupActivity.GroupContent$lambda$85((MutableState<String>)$editingAlbumId$delegate);
                if (v0 == null) {
                    v0 = targetId = $openedAlbum.getAlbumId();
                }
                if (StringsKt.isBlank((CharSequence)(finalName = StringsKt.trim((CharSequence)GroupActivity.GroupContent$lambda$88((MutableState<String>)$albumNameInput$delegate)).toString()))) {
                    Toast.makeText((Context)$albumContext, (CharSequence)"\u8bf7\u8f93\u5165\u5206\u7ec4\u540d\u79f0", (int)0).show();
                    return false;
                }
                var10_9 = finalName;
                v1 = Locale.ROOT;
                Intrinsics.checkNotNullExpressionValue((Object)v1, (String)"ROOT");
                v2 = var10_9.toLowerCase(v1);
                Intrinsics.checkNotNullExpressionValue((Object)v2, (String)"toLowerCase(...)");
                normalized = v2;
                $this$any\1 = GroupActivity.GroupContent$lambda$34((MutableState<List<GroupAlbumStore.GroupAlbum>>)$albumList$delegate);
                $i$f$any\1\1061 = false;
                if (!($this$any\1 instanceof Collection) || !((Collection)$this$any\1).isEmpty()) break block8;
                v3 = false;
                break block7;
            }
            for (T element\1 : $this$any\1) {
                it\2 = (GroupAlbumStore.GroupAlbum)element\1;
                $i$a$-any-GroupActivity$GroupContent$11$1$1$27$1$1\2\3778\0 = false;
                if (Intrinsics.areEqual((Object)it\2.getAlbumId(), (Object)targetId)) ** GOTO lbl-1000
                var16_16 = StringsKt.trim((CharSequence)it\2.getName()).toString();
                v4 = Locale.ROOT;
                Intrinsics.checkNotNullExpressionValue((Object)v4, (String)"ROOT");
                v5 = var16_16.toLowerCase(v4);
                Intrinsics.checkNotNullExpressionValue((Object)v5, (String)"toLowerCase(...)");
                if (Intrinsics.areEqual((Object)v5, (Object)normalized)) {
                    v6 = true;
                } else lbl-1000:
                // 2 sources

                {
                    v6 = false;
                }
                if (!v6) continue;
                v3 = true;
                break block7;
            }
            v3 = false;
        }
        if (v3) {
            Toast.makeText((Context)$albumContext, (CharSequence)"\u5df2\u5b58\u5728\u540c\u540d\u5206\u7ec4", (int)0).show();
            return false;
        }
        if ($albumStore.updateAlbum(targetId, finalName, GroupActivity.GroupContent$lambda$91((MutableState<String>)$albumDescriptionInput$delegate))) {
            GroupActivity.GroupContent$lambda$35((MutableState<List<GroupAlbumStore.GroupAlbum>>)$albumList$delegate, $albumStore.loadAlbums());
            Toast.makeText((Context)$albumContext, (CharSequence)"\u5206\u7ec4\u5df2\u66f4\u65b0", (int)0).show();
        }
        return true;
    }

    private static final Unit GroupContent$lambda$259$lambda$258$lambda$257$lambda$246$lambda$245(GroupAlbumStore.GroupAlbum $openedAlbum, GroupAlbumStore $albumStore, boolean $openedAsStandaloneDetail, Context $albumContext, Function0 $onCloseAlbumDetail, MutableState $showDeleteAlbumConfirm$delegate, MutableState $closingStandaloneDetail$delegate, MutableState $openedAlbumId$delegate, MutableState $albumList$delegate) {
        GroupAlbumStore.GroupAlbum targetAlbum = $openedAlbum;
        boolean deleted = $albumStore.deleteAlbum(targetAlbum.getAlbumId());
        GroupActivity.GroupContent$lambda$80((MutableState<Boolean>)$showDeleteAlbumConfirm$delegate, false);
        if (deleted) {
            if ($openedAsStandaloneDetail) {
                GroupActivity.GroupContent$lambda$83((MutableState<Boolean>)$closingStandaloneDetail$delegate, true);
            } else {
                GroupActivity.GroupContent$lambda$38((MutableState<String>)$openedAlbumId$delegate, null);
            }
            GroupActivity.GroupContent$lambda$35((MutableState<List<GroupAlbumStore.GroupAlbum>>)$albumList$delegate, $albumStore.loadAlbums());
            Toast.makeText((Context)$albumContext, (CharSequence)"\u5206\u7ec4\u5df2\u5220\u9664", (int)0).show();
            if ($openedAsStandaloneDetail) {
                $onCloseAlbumDetail.invoke();
            }
        } else {
            Toast.makeText((Context)$albumContext, (CharSequence)"\u5220\u9664\u5931\u8d25\uff0c\u8bf7\u91cd\u8bd5", (int)0).show();
        }
        return Unit.INSTANCE;
    }

    private static final Unit GroupContent$lambda$259$lambda$258$lambda$257$lambda$248$lambda$247(MutableState $showDeleteAlbumConfirm$delegate) {
        GroupActivity.GroupContent$lambda$80((MutableState<Boolean>)$showDeleteAlbumConfirm$delegate, false);
        return Unit.INSTANCE;
    }

    private static final Unit GroupContent$lambda$259$lambda$258$lambda$257$lambda$250$lambda$249(GroupAlbumStore $albumStore, GroupAlbumStore.GroupAlbum $openedAlbum, Context $albumContext, boolean $removingAll, MutableState $selectedAlbumRecordIds$delegate, MutableState $showRemoveFromAlbumConfirm$delegate, MutableState $albumList$delegate, MutableState $albumSelectionModeActive$delegate) {
        boolean removed = $albumStore.removeRecordIds($openedAlbum.getAlbumId(), (Collection<String>)GroupActivity.GroupContent$lambda$64((MutableState<Set<String>>)$selectedAlbumRecordIds$delegate));
        GroupActivity.GroupContent$lambda$71((MutableState<Boolean>)$showRemoveFromAlbumConfirm$delegate, false);
        if (removed) {
            GroupActivity.GroupContent$lambda$35((MutableState<List<GroupAlbumStore.GroupAlbum>>)$albumList$delegate, $albumStore.loadAlbums());
            GroupActivity.GroupContent$lambda$68((MutableState<Boolean>)$albumSelectionModeActive$delegate, false);
            GroupActivity.GroupContent$lambda$65((MutableState<Set<String>>)$selectedAlbumRecordIds$delegate, SetsKt.emptySet());
            Toast.makeText((Context)$albumContext, (CharSequence)($removingAll ? "\u5df2\u5168\u90e8\u79fb\u51fa" : "\u5df2\u79fb\u51fa\u5206\u7ec4"), (int)0).show();
        } else {
            Toast.makeText((Context)$albumContext, (CharSequence)"\u79fb\u51fa\u5931\u8d25\uff0c\u8bf7\u91cd\u8bd5", (int)0).show();
        }
        return Unit.INSTANCE;
    }

    private static final Unit GroupContent$lambda$259$lambda$258$lambda$257$lambda$252$lambda$251(MutableState $showRemoveFromAlbumConfirm$delegate) {
        GroupActivity.GroupContent$lambda$71((MutableState<Boolean>)$showRemoveFromAlbumConfirm$delegate, false);
        return Unit.INSTANCE;
    }

    private static final Unit GroupContent$lambda$259$lambda$258$lambda$257$lambda$254$lambda$253(Function1 $onDeleteRecords, MutableState $selectedAlbumRecordIds$delegate, MutableState $showDeleteSelectedConfirm$delegate, MutableState $albumSelectionModeActive$delegate) {
        $onDeleteRecords.invoke(GroupActivity.GroupContent$lambda$64((MutableState<Set<String>>)$selectedAlbumRecordIds$delegate));
        GroupActivity.GroupContent$lambda$74((MutableState<Boolean>)$showDeleteSelectedConfirm$delegate, false);
        GroupActivity.GroupContent$lambda$68((MutableState<Boolean>)$albumSelectionModeActive$delegate, false);
        GroupActivity.GroupContent$lambda$65((MutableState<Set<String>>)$selectedAlbumRecordIds$delegate, SetsKt.emptySet());
        return Unit.INSTANCE;
    }

    private static final Unit GroupContent$lambda$259$lambda$258$lambda$257$lambda$256$lambda$255(MutableState $showDeleteSelectedConfirm$delegate) {
        GroupActivity.GroupContent$lambda$74((MutableState<Boolean>)$showDeleteSelectedConfirm$delegate, false);
        return Unit.INSTANCE;
    }

    /*
     * WARNING - void declaration
     */
    @Composable
    @ComposableTarget(applier="androidx.compose.ui.UiComposable")
    private static final Unit GroupContent$lambda$259$lambda$258(GroupAlbumStore.GroupAlbum $openedAlbum, Function0 $onOpenMemory, Function0 $onOpenReminder, Function0 $onAddClick, NoMemoDockTab $startupDockPulseTab, List $selectedAlbumRecords, boolean $allOpenedRecordsSelected, GroupActivity this$0, Context $albumContext, Function0 $onOpenSettings, List $openedRecords, SettingsStore $settingsStore, GroupAlbumStore $albumStore, List $filteredExistingRecords, boolean $openedAsStandaloneDetail, Function0 $onCloseAlbumDetail, Function1 $onDeleteRecords, boolean $showAddSheet, Function0 $onDismissAddSheet, float $groupExpandedTitleAlpha, float $groupExpandedTitleTranslateY, LazyListState $groupListState, float $groupListSpacing, List $albumRows, Map $albumPreviewRecordsMap, Function1 $onOpenAlbumDetail, int $albumColumns, LazyListState $albumDetailListState, NoMemoPalette $albumPalette, Function1 $onOpenDetail, NoMemoAdaptiveSpec $albumAdaptive, MutableState $closingStandaloneDetail$delegate, Function0 $onOpenSearch, float $groupCollapsedTitleAlpha, MutableState $groupListMoreExpanded$delegate, MutableState $groupListMoreAnchorBounds$delegate, State $groupExpandedTitleHeight$delegate, MutableState $albumList$delegate, State $groupListTopPadding$delegate, MutableState $albumSelectionModeActive$delegate, MutableState $selectedAlbumRecordIds$delegate, MutableState $showRemoveFromAlbumConfirm$delegate, MutableState $showDeleteSelectedConfirm$delegate, MutableState $openedAlbumId$delegate, MutableState $detailMoreExpanded$delegate, MutableState $detailMoreAnchorBounds$delegate, MutableState $selectedExistingRecordIds$delegate, MutableState $addExistingSearchQuery$delegate, MutableState $showAddExistingSheet$delegate, MutableState $showCreateAlbumDialog$delegate, MutableState $editingAlbumId$delegate, MutableState $albumNameInput$delegate, MutableState $albumDescriptionInput$delegate, MutableState $showEditAlbumDialog$delegate, MutableState $showDeleteAlbumConfirm$delegate, MutableState $albumAutoClassifyEnabledInput$delegate, BoxScope $this$ResponsiveContentFrame, NoMemoAdaptiveSpec spec, Composer $composer, int $changed) {
        Intrinsics.checkNotNullParameter((Object)$this$ResponsiveContentFrame, (String)"$this$ResponsiveContentFrame");
        Intrinsics.checkNotNullParameter((Object)spec, (String)"spec");
        ComposerKt.sourceInformation((Composer)$composer, (String)"CN(spec)517@22299L37232:GroupActivity.kt#83vr7l");
        int $dirty = $changed;
        if (($changed & 0x30) == 0) {
            $dirty |= $composer.changed((Object)spec) ? 32 : 16;
        }
        if ($composer.shouldExecute(($dirty & 0x91) != 144, $dirty & 1)) {
            void $changed\8;
            Function0 function0;
            Function0 function02;
            Object object;
            void $this$cache\183;
            Object object2;
            Function0 function03;
            Object object3;
            void $this$cache\177;
            Object object4;
            void $this$cache\174;
            Object object5;
            Function0 function043;
            Object object6;
            void $this$cache\168;
            Object object7;
            void $this$cache\165;
            Object object8;
            Function0 function05;
            Object object9;
            int n;
            int n2;
            String string2;
            void $this$cache\162;
            Object object10;
            Function0 function06;
            Object object11;
            void $composer\23;
            void $changed\18;
            void $changed\17;
            void modifier\17;
            Function1 function1;
            void $changed\16;
            void $composer\16;
            void other\13;
            void arg0\13;
            Object object12;
            Object object13;
            Object object14;
            void $this$cache\9;
            void $composer\8;
            void $changed\3;
            void $changed\2;
            void modifier\2;
            void modifier\1;
            void $composer\1;
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart((int)-2071553610, (int)$dirty, (int)-1, (String)"com.han.nomemo.GroupActivity.GroupContent.<anonymous>.<anonymous> (GroupActivity.kt:517)");
            }
            Modifier modifier = SizeKt.fillMaxSize$default((Modifier)((Modifier)Modifier.Companion), (float)0.0f, (int)1, null);
            Composer composer = $composer;
            int n3 = 6;
            boolean bl = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\1, (int)1042775818, (String)"CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo");
            Alignment alignment = Alignment.Companion.getTopStart();
            boolean bl2 = false;
            MeasurePolicy measurePolicy = BoxKt.maybeCachedBoxMeasurePolicy((Alignment)alignment, (boolean)bl2);
            void var68_68 = modifier\1;
            int n4 = 0x70 & n3 << 3;
            boolean bl3 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\1, (int)-1159599143, (String)"CC(Layout)P(!1,2)80@3267L27,83@3433L360:Layout.kt#80mrfh");
            int n5 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode((Composer)$composer\1, (int)0));
            CompositionLocalMap compositionLocalMap = $composer\1.getCurrentCompositionLocalMap();
            Modifier modifier2 = ComposedModifierKt.materializeModifier((Composer)$composer\1, (Modifier)modifier\2);
            Function0 function07 = ComposeUiNode.Companion.getConstructor();
            int n6 = 6 | 0x380 & $changed\2 << 6;
            boolean bl4 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\1, (int)-553112988, (String)"CC(ReusableComposeNode)N(factory,update,content)399@15590L9:Composables.kt#9igjgp");
            if (!($composer\1.getApplier() instanceof Applier)) {
                ComposablesKt.invalidApplier();
            }
            $composer\1.startReusableNode();
            if ($composer\1.getInserting()) {
                void factory\3;
                $composer\1.createNode((Function0)factory\3);
            } else {
                $composer\1.useNode();
            }
            Composer composer2 = Updater.constructor-impl((Composer)$composer\1);
            boolean bl5 = false;
            Updater.set-impl((Composer)composer2, (Object)measurePolicy, (Function2)ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.set-impl((Composer)composer2, (Object)compositionLocalMap, (Function2)ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            Function2 function2 = ComposeUiNode.Companion.getSetCompositeKeyHash();
            boolean bl6 = false;
            Composer composer3 = composer2;
            boolean bl7 = false;
            if (composer3.getInserting() || !Intrinsics.areEqual((Object)composer3.rememberedValue(), (Object)n5)) {
                composer3.updateRememberedValue((Object)n5);
                composer2.apply((Object)n5, function2);
            }
            Updater.set-impl((Composer)composer2, (Object)modifier2, (Function2)ComposeUiNode.Companion.getSetModifier());
            int n7 = 0xE & $changed\3 >> 6;
            void $composer\7 = $composer\1;
            boolean bl8 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\7, (int)1833054614, (String)"C72@3469L9:Box.kt#2w3rfo");
            int n8 = 6 | 0x70 & n3 >> 6;
            void var87_87 = $composer\7;
            BoxScope boxScope = (BoxScope)BoxScopeInstance.INSTANCE;
            boolean bl9 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\8, (int)-1776719480, (String)"C519@22451L98,519@22429L120,523@22571L18209,866@43166L33,872@43513L166,880@43919L202,887@44312L40,888@44396L154,864@43022L1606,898@44791L30,904@45140L410,915@45786L301,925@46324L488,939@47103L164,896@44650L2695:GroupActivity.kt#83vr7l");
            Object object15 = null;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\8, (int)-1442822670, (String)"CC(remember):GroupActivity.kt#9igjgp");
            void var90_90 = $composer\8;
            boolean bl10 = false;
            boolean $i$f$cache\9\5212 = false;
            Object it\102 = $this$cache\9.rememberedValue();
            boolean bl11 = false;
            if (it\102 == Composer.Companion.getEmpty()) {
                object14 = object15;
                boolean bl12 = false;
                object13 = GroupActivity::GroupContent$lambda$259$lambda$258$lambda$257$lambda$142$lambda$141;
                object15 = object14;
                Function1 function12 = object13;
                $this$cache\9.updateRememberedValue((Object)function12);
                object12 = function12;
            } else {
                object12 = it\102;
            }
            Function1 function13 = (Function1)object12;
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\8);
            LayerBackdrop layerBackdrop = LayerBackdropKt.rememberLayerBackdrop(object15, (Function1)function13, (Composer)$composer\8, (int)48, (int)1);
            float f = spec.getPageTopPadding-D9Ej5fM();
            int $this$dp\132 = 4;
            boolean bl13 = false;
            float $this$dp\132 = Dp.constructor-impl((float)$this$dp\132);
            boolean bl14 = false;
            boolean bl15 = false;
            boolean bl16 = false;
            boolean $this$dp\162 = false;
            boolean bl17 = false;
            Modifier $this$dp\162 = PaddingKt.padding-qDBjuR0((Modifier)WindowInsetsPadding_androidKt.statusBarsPadding((Modifier)LayerBackdropModifierKt.layerBackdrop((Modifier)SizeKt.fillMaxSize$default((Modifier)((Modifier)Modifier.Companion), (float)0.0f, (int)1, null), (LayerBackdrop)layerBackdrop)), (float)spec.getPageHorizontalPadding-D9Ej5fM(), (float)((Dp)RangesKt.coerceAtLeast((Comparable)Dp.box-impl((float)Dp.constructor-impl((float)(arg0\13 - other\13))), (Comparable)Dp.box-impl((float)Dp.constructor-impl((float)((float)bl15))))).unbox-impl(), (float)spec.getPageHorizontalPadding-D9Ej5fM(), (float)Dp.constructor-impl((float)((float)$this$dp\162)));
            void $i$f$cache\9\5212 = $composer\8;
            boolean it\102 = false;
            boolean bl18 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\16, (int)1341605231, (String)"CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo");
            Arrangement.Vertical vertical = Arrangement.INSTANCE.getTop();
            Alignment.Horizontal horizontal = Alignment.Companion.getStart();
            MeasurePolicy measurePolicy2 = ColumnKt.columnMeasurePolicy((Arrangement.Vertical)vertical, (Alignment.Horizontal)horizontal, (Composer)$composer\16, (int)(0xE & $changed\16 >> 3 | 0x70 & $changed\16 >> 3));
            void var100_210 = function1;
            int n9 = 0x70 & $changed\16 << 3;
            boolean bl19 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\16, (int)-1159599143, (String)"CC(Layout)P(!1,2)80@3267L27,83@3433L360:Layout.kt#80mrfh");
            int n10 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode((Composer)$composer\16, (int)0));
            CompositionLocalMap compositionLocalMap2 = $composer\16.getCurrentCompositionLocalMap();
            Modifier modifier3 = ComposedModifierKt.materializeModifier((Composer)$composer\16, (Modifier)modifier\17);
            Function0 function08 = ComposeUiNode.Companion.getConstructor();
            int n11 = 6 | 0x380 & $changed\17 << 6;
            boolean bl20 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\16, (int)-553112988, (String)"CC(ReusableComposeNode)N(factory,update,content)399@15590L9:Composables.kt#9igjgp");
            if (!($composer\16.getApplier() instanceof Applier)) {
                ComposablesKt.invalidApplier();
            }
            $composer\16.startReusableNode();
            if ($composer\16.getInserting()) {
                void factory\18;
                $composer\16.createNode((Function0)factory\18);
            } else {
                $composer\16.useNode();
            }
            Composer composer4 = Updater.constructor-impl((Composer)$composer\16);
            boolean bl21 = false;
            Updater.set-impl((Composer)composer4, (Object)measurePolicy2, (Function2)ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.set-impl((Composer)composer4, (Object)compositionLocalMap2, (Function2)ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            Function2 function22 = ComposeUiNode.Companion.getSetCompositeKeyHash();
            boolean bl22 = false;
            Composer composer5 = composer4;
            boolean bl23 = false;
            if (composer5.getInserting() || !Intrinsics.areEqual((Object)composer5.rememberedValue(), (Object)n10)) {
                composer5.updateRememberedValue((Object)n10);
                composer4.apply((Object)n10, function22);
            }
            Updater.set-impl((Composer)composer4, (Object)modifier3, (Function2)ComposeUiNode.Companion.getSetModifier());
            int n12 = 0xE & $changed\18 >> 6;
            void $composer\22 = $composer\16;
            boolean bl24 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\22, (int)2093002350, (String)"C89@4557L9:Column.kt#2w3rfo");
            int n13 = 6 | 0x70 & $changed\16 >> 6;
            void var119_272 = $composer\22;
            ColumnScope columnScope = (ColumnScope)ColumnScopeInstance.INSTANCE;
            boolean bl25 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\23, (int)1562814446, (String)"C:GroupActivity.kt#83vr7l");
            if ($openedAlbum == null && !GroupActivity.GroupContent$lambda$82((MutableState<Boolean>)$closingStandaloneDetail$delegate)) {
                void $composer\55;
                void $changed\50;
                void $changed\49;
                void modifier\49;
                void modifier\48;
                void $changed\48;
                void $composer\48;
                Object object16;
                Function1 function14;
                Function1 function15;
                Modifier modifier4;
                void $this$cache\45;
                Object object17;
                void n35;
                Object object18;
                Function1 function16;
                void $this$cache\38;
                Object object19;
                Function0 function09;
                void $this$cache\35;
                Object object20;
                Function0 function010;
                NoMemoAdaptiveSpec noMemoAdaptiveSpec;
                Modifier modifier5;
                void $this$cache\32;
                void $composer\31;
                int n14;
                Function0 function011;
                int n15;
                void modifier\25;
                void $changed\24;
                void modifier\24;
                void $composer\24;
                $composer\23.startReplaceGroup(1562504817);
                ComposerKt.sourceInformation((Composer)$composer\23, (String)"536@23246L1560,568@25125L195,563@24835L903");
                Modifier modifier6 = SizeKt.height-3ABfNKs((Modifier)SizeKt.fillMaxWidth$default((Modifier)((Modifier)Modifier.Companion), (float)0.0f, (int)1, null), (float)spec.getTopActionButtonSize-D9Ej5fM());
                void var123_290 = $composer\23;
                boolean bl26 = false;
                boolean bl27 = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\24, (int)1042775818, (String)"CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo");
                Alignment alignment2 = Alignment.Companion.getTopStart();
                boolean bl28 = false;
                MeasurePolicy measurePolicy3 = BoxKt.maybeCachedBoxMeasurePolicy((Alignment)alignment2, (boolean)bl28);
                void var129_344 = modifier\24;
                int n16 = 0x70 & $changed\24 << 3;
                boolean bl29 = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\24, (int)-1159599143, (String)"CC(Layout)P(!1,2)80@3267L27,83@3433L360:Layout.kt#80mrfh");
                int n17 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode((Composer)$composer\24, (int)0));
                CompositionLocalMap compositionLocalMap3 = $composer\24.getCurrentCompositionLocalMap();
                Modifier modifier7 = ComposedModifierKt.materializeModifier((Composer)$composer\24, (Modifier)modifier\25);
                Function0 function012 = ComposeUiNode.Companion.getConstructor();
                int n18 = 6 | 0x380 & n15 << 6;
                boolean bl30 = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\24, (int)-553112988, (String)"CC(ReusableComposeNode)N(factory,update,content)399@15590L9:Composables.kt#9igjgp");
                if (!($composer\24.getApplier() instanceof Applier)) {
                    ComposablesKt.invalidApplier();
                }
                $composer\24.startReusableNode();
                if ($composer\24.getInserting()) {
                    $composer\24.createNode(function011);
                } else {
                    $composer\24.useNode();
                }
                Composer composer6 = Updater.constructor-impl((Composer)$composer\24);
                boolean bl31 = false;
                Updater.set-impl((Composer)composer6, (Object)measurePolicy3, (Function2)ComposeUiNode.Companion.getSetMeasurePolicy());
                Updater.set-impl((Composer)composer6, (Object)compositionLocalMap3, (Function2)ComposeUiNode.Companion.getSetResolvedCompositionLocals());
                Function2 function23 = ComposeUiNode.Companion.getSetCompositeKeyHash();
                boolean bl32 = false;
                Composer composer7 = composer6;
                boolean bl33 = false;
                if (composer7.getInserting() || !Intrinsics.areEqual((Object)composer7.rememberedValue(), (Object)n17)) {
                    composer7.updateRememberedValue((Object)n17);
                    composer6.apply((Object)n17, function23);
                }
                Updater.set-impl((Composer)composer6, (Object)modifier7, (Function2)ComposeUiNode.Companion.getSetModifier());
                int n19 = 0xE & n14 >> 6;
                void $composer\30 = $composer\24;
                boolean bl34 = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\30, (int)1833054614, (String)"C72@3469L9:Box.kt#2w3rfo");
                int n20 = 6 | 0x70 & $changed\24 >> 6;
                void var148_393 = $composer\30;
                BoxScope boxScope2 = (BoxScope)BoxScopeInstance.INSTANCE;
                boolean bl35 = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\31, (int)-1629556029, (String)"C543@23614L164,547@23830L50,548@23946L34,541@23489L608,552@24179L41,558@24622L120,551@24130L646:GroupActivity.kt#83vr7l");
                Object object21 = boxScope2.align((Modifier)Modifier.Companion, Alignment.Companion.getTopStart());
                NoMemoAdaptiveSpec noMemoAdaptiveSpec2 = spec;
                Modifier modifier8 = object21;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\31, (int)-468205443, (String)"CC(remember):GroupActivity.kt#9igjgp");
                void var152_408 = $composer\31;
                boolean bl36 = $composer\31.changed((Object)$onOpenSearch);
                boolean bl37 = false;
                Object object22 = $this$cache\32.rememberedValue();
                boolean bl38 = false;
                if (bl36 || object22 == Composer.Companion.getEmpty()) {
                    modifier5 = modifier8;
                    noMemoAdaptiveSpec = noMemoAdaptiveSpec2;
                    boolean bl39 = false;
                    function010 = () -> GroupActivity.GroupContent$lambda$259$lambda$258$lambda$257$lambda$188$lambda$151$lambda$144$lambda$143($onOpenSearch, $groupListMoreExpanded$delegate);
                    noMemoAdaptiveSpec2 = noMemoAdaptiveSpec;
                    modifier8 = modifier5;
                    Function0 function013 = function010;
                    $this$cache\32.updateRememberedValue((Object)function013);
                    object20 = function013;
                } else {
                    object20 = object22;
                }
                Function0 function014 = (Function0)object20;
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\31);
                Function0 function015 = function014;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\31, (int)-468198645, (String)"CC(remember):GroupActivity.kt#9igjgp");
                $this$cache\32 = $composer\31;
                boolean bl40 = false;
                boolean bl41 = false;
                Object object23 = $this$cache\35.rememberedValue();
                boolean bl42 = false;
                if (object23 == Composer.Companion.getEmpty()) {
                    function010 = function015;
                    modifier5 = modifier8;
                    noMemoAdaptiveSpec = noMemoAdaptiveSpec2;
                    boolean bl43 = false;
                    function09 = () -> GroupActivity.GroupContent$lambda$259$lambda$258$lambda$257$lambda$188$lambda$151$lambda$146$lambda$145($groupListMoreExpanded$delegate);
                    noMemoAdaptiveSpec2 = noMemoAdaptiveSpec;
                    modifier8 = modifier5;
                    function015 = function010;
                    Function0 function016 = function09;
                    $this$cache\35.updateRememberedValue((Object)function016);
                    object19 = function016;
                } else {
                    object19 = object23;
                }
                function014 = (Function0)object19;
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\31);
                Function0 function017 = function014;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\31, (int)-468194949, (String)"CC(remember):GroupActivity.kt#9igjgp");
                $this$cache\35 = $composer\31;
                boolean invalid\4022 = false;
                boolean bl44 = false;
                Object object24 = $this$cache\38.rememberedValue();
                boolean $i$a$-let-ComposerKt$cache$1\39\3912\392 = false;
                if (object24 == Composer.Companion.getEmpty()) {
                    function09 = function017;
                    function010 = function015;
                    modifier5 = modifier8;
                    noMemoAdaptiveSpec = noMemoAdaptiveSpec2;
                    boolean bl45 = false;
                    Function1 function17 = arg_0 -> GroupActivity.GroupContent$lambda$259$lambda$258$lambda$257$lambda$188$lambda$151$lambda$148$lambda$147($groupListMoreAnchorBounds$delegate, arg_0);
                    noMemoAdaptiveSpec2 = noMemoAdaptiveSpec;
                    modifier8 = modifier5;
                    function015 = function010;
                    function017 = function09;
                    function16 = function17;
                    $this$cache\38.updateRememberedValue((Object)function16);
                    object18 = function16;
                } else {
                    object18 = object24;
                }
                function014 = (Function1)object18;
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\31);
                ComposeUiKt.NoMemoTopActionButtons(noMemoAdaptiveSpec2, modifier8, (Function0<Unit>)function015, (Function0<Unit>)function017, (Function1<? super IntRect, Unit>)function014, (Composer)$composer\31, 0x6C00 | 0xE & $dirty >> 3, 0);
                object21 = StringResources_androidKt.stringResource((int)R.string.group_page_title, (Composer)$composer\31, (int)0);
                long l = $albumPalette.getTextPrimary-0d7_KjU();
                long l2 = spec.isNarrow() ? TextUnitKt.getSp((int)18) : TextUnitKt.getSp((int)19);
                object24 = FontWeight.Companion.getSemiBold();
                Object object25 = boxScope2.align((Modifier)Modifier.Companion, Alignment.Companion.getCenter());
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\31, (int)-468173231, (String)"CC(remember):GroupActivity.kt#9igjgp");
                function16 = $composer\31;
                boolean bl46 = $composer\31.changed($groupCollapsedTitleAlpha);
                boolean bl47 = false;
                Object object26 = n35.rememberedValue();
                boolean bl48 = false;
                if (bl46 || object26 == Composer.Companion.getEmpty()) {
                    noMemoAdaptiveSpec = object25;
                    boolean bl49 = false;
                    object25 = noMemoAdaptiveSpec;
                    Function1 function18 = arg_0 -> GroupActivity.GroupContent$lambda$259$lambda$258$lambda$257$lambda$188$lambda$151$lambda$150$lambda$149($groupCollapsedTitleAlpha, arg_0);
                    n35.updateRememberedValue((Object)function18);
                    object17 = function18;
                } else {
                    object17 = object26;
                }
                Function1 bl45 = (Function1)object17;
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\31);
                Modifier $i$a$-let-ComposerKt$cache$1\39\3912\392 = GraphicsLayerModifierKt.graphicsLayer((Modifier)object25, (Function1)bl45);
                TextKt.Text--4IGK_g((String)object21, (Modifier)$i$a$-let-ComposerKt$cache$1\39\3912\392, (long)l, (long)l2, null, (FontWeight)object24, null, (long)0L, null, null, (long)0L, (int)0, (boolean)false, (int)0, (int)0, null, null, (Composer)$composer\31, (int)196608, (int)0, (int)131024);
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\31);
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\30);
                $composer\24.endNode();
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\24);
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\24);
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\24);
                int $this$dp\452 = 2;
                boolean $i$f$getDp\44\5692 = false;
                Modifier modifier9 = PaddingKt.padding-qDBjuR0$default((Modifier)SizeKt.height-3ABfNKs((Modifier)SizeKt.fillMaxWidth$default((Modifier)((Modifier)Modifier.Companion), (float)0.0f, (int)1, null), (float)GroupActivity.GroupContent$lambda$126((State<Dp>)$groupExpandedTitleHeight$delegate)), (float)0.0f, (float)Dp.constructor-impl((float)$this$dp\452), (float)0.0f, (float)0.0f, (int)13, null);
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\23, (int)-88088579, (String)"CC(remember):GroupActivity.kt#9igjgp");
                void $i$f$getDp\44\5692 = $composer\23;
                boolean bl50 = $composer\23.changed($groupExpandedTitleAlpha) | $composer\23.changed($groupExpandedTitleTranslateY);
                boolean $i$f$cache\45\5702 = false;
                Object it\462 = $this$cache\45.rememberedValue();
                boolean bl51 = false;
                if (bl50 || it\462 == Composer.Companion.getEmpty()) {
                    modifier4 = modifier9;
                    boolean bl52 = false;
                    function15 = arg_0 -> GroupActivity.GroupContent$lambda$259$lambda$258$lambda$257$lambda$188$lambda$153$lambda$152($groupExpandedTitleAlpha, $groupExpandedTitleTranslateY, arg_0);
                    modifier9 = modifier4;
                    function14 = function15;
                    $this$cache\45.updateRememberedValue((Object)function14);
                    object16 = function14;
                } else {
                    object16 = it\462;
                }
                Function1 $this$dp\452 = (Function1)object16;
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\23);
                $this$dp\452 = GraphicsLayerModifierKt.graphicsLayer((Modifier)modifier9, (Function1)$this$dp\452);
                void $i$f$cache\45\5702 = $composer\23;
                boolean it\462 = false;
                boolean bl532 = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\48, (int)1341605231, (String)"CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo");
                Arrangement.Vertical vertical2 = Arrangement.INSTANCE.getTop();
                Alignment.Horizontal horizontal2 = Alignment.Companion.getStart();
                MeasurePolicy measurePolicy42 = ColumnKt.columnMeasurePolicy((Arrangement.Vertical)vertical2, (Alignment.Horizontal)horizontal2, (Composer)$composer\48, (int)(0xE & $changed\48 >> 3 | 0x70 & $changed\48 >> 3));
                function14 = modifier\48;
                n15 = 0x70 & $changed\48 << 3;
                boolean bl542 = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\48, (int)-1159599143, (String)"CC(Layout)P(!1,2)80@3267L27,83@3433L360:Layout.kt#80mrfh");
                int n21 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode((Composer)$composer\48, (int)0));
                CompositionLocalMap compositionLocalMap4 = $composer\48.getCurrentCompositionLocalMap();
                Modifier modifier10 = ComposedModifierKt.materializeModifier((Composer)$composer\48, (Modifier)modifier\49);
                function011 = ComposeUiNode.Companion.getConstructor();
                n14 = 6 | 0x380 & $changed\49 << 6;
                boolean bl552 = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\48, (int)-553112988, (String)"CC(ReusableComposeNode)N(factory,update,content)399@15590L9:Composables.kt#9igjgp");
                if (!($composer\48.getApplier() instanceof Applier)) {
                    ComposablesKt.invalidApplier();
                }
                $composer\48.startReusableNode();
                if ($composer\48.getInserting()) {
                    void factory\50;
                    $composer\48.createNode((Function0)factory\50);
                } else {
                    $composer\48.useNode();
                }
                Composer composer82 = Updater.constructor-impl((Composer)$composer\48);
                boolean bl56 = false;
                Updater.set-impl((Composer)composer82, (Object)measurePolicy42, (Function2)ComposeUiNode.Companion.getSetMeasurePolicy());
                Updater.set-impl((Composer)composer82, (Object)compositionLocalMap4, (Function2)ComposeUiNode.Companion.getSetResolvedCompositionLocals());
                Function2 function24 = ComposeUiNode.Companion.getSetCompositeKeyHash();
                boolean bl57 = false;
                Composer composer9 = composer82;
                boolean bl58 = false;
                if (composer9.getInserting() || !Intrinsics.areEqual((Object)composer9.rememberedValue(), (Object)n21)) {
                    composer9.updateRememberedValue((Object)n21);
                    composer82.apply((Object)n21, function24);
                }
                Updater.set-impl((Composer)composer82, (Object)modifier10, (Function2)ComposeUiNode.Companion.getSetModifier());
                int n22 = 0xE & $changed\50 >> 6;
                void $composer\54 = $composer\48;
                int n23 = 0;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\54, (int)2093002350, (String)"C89@4557L9:Column.kt#2w3rfo");
                int n24 = 6 | 0x70 & $changed\48 >> 6;
                $composer\31 = $composer\54;
                ColumnScope columnScope22 = (ColumnScope)ColumnScopeInstance.INSTANCE;
                boolean bl592 = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\55, (int)-218596745, (String)"C574@25434L41,573@25385L323:GroupActivity.kt#83vr7l");
                TextKt.Text--4IGK_g((String)StringResources_androidKt.stringResource((int)R.string.group_page_title, (Composer)$composer\55, (int)0), null, (long)$albumPalette.getTextPrimary-0d7_KjU(), (long)spec.getTitleSize-XSAIIZE(), null, (FontWeight)FontWeight.Companion.getBold(), null, (long)0L, null, null, (long)0L, (int)0, (boolean)false, (int)0, (int)0, null, null, (Composer)$composer\55, (int)196608, (int)0, (int)131026);
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\55);
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\54);
                $composer\48.endNode();
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\48);
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\48);
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\48);
                if (GroupActivity.GroupContent$lambda$34((MutableState<List<GroupAlbumStore.GroupAlbum>>)$albumList$delegate).isEmpty()) {
                    void $composer\71;
                    void $changed\66;
                    void $changed\65;
                    void modifier\65;
                    void $changed\64;
                    void modifier\64;
                    void contentAlignment\64;
                    void $composer\64;
                    void other\642;
                    void arg0\642;
                    float f2;
                    float f3;
                    float f4;
                    float f5;
                    float f6;
                    $composer\23.startReplaceGroup(1564910634);
                    ComposerKt.sourceInformation((Composer)$composer\23, (String)"584@26009L808");
                    if (spec.isNarrow()) {
                        int n25 = 56;
                        boolean bl60 = false;
                        f6 = Dp.constructor-impl((float)n25);
                    } else {
                        int n26 = 60;
                        boolean bl61 = false;
                        f6 = f5 = Dp.constructor-impl((float)n26);
                    }
                    if (spec.isNarrow()) {
                        int n27 = 10;
                        boolean bl62 = false;
                        f4 = Dp.constructor-impl((float)n27);
                    } else {
                        int n28 = 14;
                        boolean bl63 = false;
                        f4 = Dp.constructor-impl((float)n28);
                    }
                    float f7 = f4;
                    float n28 = spec.getPageBottomPadding-D9Ej5fM();
                    float f8 = f5;
                    boolean bl64 = false;
                    f3 = Dp.constructor-impl((float)(f3 + f8));
                    float f9 = f7;
                    boolean bl65 = false;
                    f2 = Dp.constructor-impl((float)(f2 + f9));
                    int $this$dp\632 = 8;
                    boolean bl66 = false;
                    float $this$dp\632 = Dp.constructor-impl((float)$this$dp\632);
                    boolean bl67 = false;
                    Modifier arg0\642 = PaddingKt.padding-qDBjuR0$default((Modifier)ColumnScope.weight$default((ColumnScope)columnScope, (Modifier)SizeKt.fillMaxWidth$default((Modifier)((Modifier)Modifier.Companion), (float)0.0f, (int)1, null), (float)1.0f, (boolean)false, (int)2, null), (float)0.0f, (float)0.0f, (float)0.0f, (float)Dp.constructor-impl((float)(arg0\642 + other\642)), (int)7, null);
                    Alignment other\642 = Alignment.Companion.getCenter();
                    void bl532 = $composer\23;
                    int measurePolicy42 = 48;
                    boolean bl68 = false;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer\64, (int)1042775818, (String)"CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo");
                    boolean bl69 = false;
                    MeasurePolicy measurePolicy5 = BoxKt.maybeCachedBoxMeasurePolicy((Alignment)contentAlignment\64, (boolean)bl69);
                    void bl542 = modifier\64;
                    n21 = 0x70 & $changed\64 << 3;
                    boolean bl70 = false;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer\64, (int)-1159599143, (String)"CC(Layout)P(!1,2)80@3267L27,83@3433L360:Layout.kt#80mrfh");
                    int n29 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode((Composer)$composer\64, (int)0));
                    CompositionLocalMap compositionLocalMap5 = $composer\64.getCurrentCompositionLocalMap();
                    Modifier modifier11 = ComposedModifierKt.materializeModifier((Composer)$composer\64, (Modifier)modifier\65);
                    Function0 bl552 = ComposeUiNode.Companion.getConstructor();
                    int composer82 = 6 | 0x380 & $changed\65 << 6;
                    boolean bl71 = false;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer\64, (int)-553112988, (String)"CC(ReusableComposeNode)N(factory,update,content)399@15590L9:Composables.kt#9igjgp");
                    if (!($composer\64.getApplier() instanceof Applier)) {
                        ComposablesKt.invalidApplier();
                    }
                    $composer\64.startReusableNode();
                    if ($composer\64.getInserting()) {
                        void factory\66;
                        $composer\64.createNode((Function0)factory\66);
                    } else {
                        $composer\64.useNode();
                    }
                    Composer composer10 = Updater.constructor-impl((Composer)$composer\64);
                    boolean bl72 = false;
                    Updater.set-impl((Composer)composer10, (Object)measurePolicy5, (Function2)ComposeUiNode.Companion.getSetMeasurePolicy());
                    Updater.set-impl((Composer)composer10, (Object)compositionLocalMap5, (Function2)ComposeUiNode.Companion.getSetResolvedCompositionLocals());
                    Function2 function25 = ComposeUiNode.Companion.getSetCompositeKeyHash();
                    boolean bl73 = false;
                    Composer composer11 = composer10;
                    boolean bl74 = false;
                    if (composer11.getInserting() || !Intrinsics.areEqual((Object)composer11.rememberedValue(), (Object)n29)) {
                        composer11.updateRememberedValue((Object)n29);
                        composer10.apply((Object)n29, function25);
                    }
                    Updater.set-impl((Composer)composer10, (Object)modifier11, (Function2)ComposeUiNode.Companion.getSetModifier());
                    n23 = 0xE & $changed\66 >> 6;
                    void n37 = $composer\64;
                    boolean bl75 = false;
                    ComposerKt.sourceInformationMarkerStart((Composer)n37, (int)1833054614, (String)"C72@3469L9:Box.kt#2w3rfo");
                    int columnScope22 = 6 | 0x70 & $changed\64 >> 6;
                    void bl592 = n37;
                    BoxScope $this$GroupContent_u24lambda_u24259_u24lambda_u24258_u24lambda_u24257_u24lambda_u24188_u24lambda_u24155\7322 = (BoxScope)BoxScopeInstance.INSTANCE;
                    boolean bl76 = false;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer\71, (int)1919401987, (String)"C592@26525L258:GroupActivity.kt#83vr7l");
                    ComposeUiKt.NoMemoEmptyState(R.drawable.ic_nm_group_dock, "\u8fd8\u6ca1\u6709\u5206\u7ec4", null, "\u70b9\u51fb\u53f3\u4e0a\u89d2\u66f4\u591a\u83dc\u5355\u65b0\u589e\u5206\u7ec4", (Composer)$composer\71, 3120, 4);
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer\71);
                    ComposerKt.sourceInformationMarkerEnd((Composer)n37);
                    $composer\64.endNode();
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer\64);
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer\64);
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer\64);
                    $composer\23.endReplaceGroup();
                } else {
                    Object object27;
                    void $this$cache\74;
                    void other\73;
                    void arg0\742;
                    $composer\23.startReplaceGroup(1565999168);
                    ComposerKt.sourceInformation((Composer)$composer\23, (String)"607@27412L1659,599@26887L2184");
                    Modifier modifier12 = ColumnScope.weight$default((ColumnScope)columnScope, (Modifier)((Modifier)Modifier.Companion), (float)1.0f, (boolean)false, (int)2, null);
                    LazyListState lazyListState = $groupListState;
                    float f5 = spec.getPageBottomPadding-D9Ej5fM();
                    int $this$dp\732 = 24;
                    boolean bl77 = false;
                    float $this$dp\732 = Dp.constructor-impl((float)$this$dp\732);
                    boolean bl78 = false;
                    PaddingValues paddingValues = PaddingKt.PaddingValues-a9UjIt4$default((float)0.0f, (float)GroupActivity.GroupContent$lambda$127((State<Dp>)$groupListTopPadding$delegate), (float)0.0f, (float)Dp.constructor-impl((float)(arg0\742 + other\73)), (int)5, null);
                    boolean bl79 = false;
                    Arrangement.Vertical vertical3 = (Arrangement.Vertical)Arrangement.INSTANCE.spacedBy-0680j_4($groupListSpacing);
                    Alignment.Horizontal horizontal3 = null;
                    FlingBehavior flingBehavior = null;
                    boolean bl80 = false;
                    OverscrollEffect overscrollEffect = null;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer\23, (int)-88013931, (String)"CC(remember):GroupActivity.kt#9igjgp");
                    other\73 = $composer\23;
                    boolean bl81 = $composer.changedInstance((Object)$albumRows) | $composer.changedInstance((Object)this$0) | ($dirty & 0x70) == 32 | $composer.changedInstance((Object)$albumPreviewRecordsMap) | $composer.changed((Object)$onOpenAlbumDetail) | $composer.changed($albumColumns);
                    boolean bl82 = false;
                    Object object28 = $this$cache\74.rememberedValue();
                    boolean bl83 = false;
                    if (bl81 || object28 == Composer.Companion.getEmpty()) {
                        OverscrollEffect overscrollEffect2 = overscrollEffect;
                        boolean bl84 = bl80;
                        FlingBehavior flingBehavior2 = flingBehavior;
                        Alignment.Horizontal horizontal4 = horizontal3;
                        Arrangement.Vertical vertical4 = vertical3;
                        boolean bl85 = bl79;
                        PaddingValues paddingValues2 = paddingValues;
                        function15 = lazyListState;
                        modifier4 = modifier12;
                        boolean bl86 = false;
                        Function1 function19 = arg_0 -> GroupActivity.GroupContent$lambda$259$lambda$258$lambda$257$lambda$188$lambda$162$lambda$161($albumRows, $albumColumns, this$0, spec, $albumPreviewRecordsMap, $onOpenAlbumDetail, $groupListMoreExpanded$delegate, arg_0);
                        modifier12 = modifier4;
                        lazyListState = function15;
                        paddingValues = paddingValues2;
                        bl79 = bl85;
                        vertical3 = vertical4;
                        horizontal3 = horizontal4;
                        flingBehavior = flingBehavior2;
                        bl80 = bl84;
                        overscrollEffect = overscrollEffect2;
                        Function1 function110 = function19;
                        $this$cache\74.updateRememberedValue((Object)function110);
                        object27 = function110;
                    } else {
                        object27 = object28;
                    }
                    Function1 arg0\742 = (Function1)object27;
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer\23);
                    LazyDslKt.LazyColumn((Modifier)modifier12, (LazyListState)lazyListState, (PaddingValues)paddingValues, (boolean)bl79, (Arrangement.Vertical)vertical3, horizontal3, flingBehavior, (boolean)bl80, overscrollEffect, (Function1)arg0\742, (Composer)$composer\23, (int)24576, (int)488);
                    $composer\23.endReplaceGroup();
                }
                $composer\23.endReplaceGroup();
            } else if ($openedAlbum != null) {
                Function1 function111;
                void $composer\100;
                int n30;
                Function0 function018;
                int n31;
                void $composer\84;
                int n32;
                Function0 function019;
                int n33;
                void modifier\78;
                int n34;
                void $composer\77;
                $composer\23.startReplaceGroup(1568572540);
                ComposerKt.sourceInformation((Composer)$composer\23, (String)"");
                if (GroupActivity.GroupContent$lambda$67((MutableState<Boolean>)$albumSelectionModeActive$delegate)) {
                    Object object29;
                    void $this$cache\88;
                    Object object30;
                    Function0 function020;
                    int n35;
                    String string3;
                    FontWeight fontWeight;
                    void modifier\77;
                    $composer\23.startReplaceGroup(1568369893);
                    ComposerKt.sourceInformation((Composer)$composer\23, (String)"635@29252L2905,679@32190L41");
                    Modifier arg0\742 = SizeKt.height-3ABfNKs((Modifier)SizeKt.fillMaxWidth$default((Modifier)((Modifier)Modifier.Companion), (float)0.0f, (int)1, null), (float)spec.getTopActionButtonSize-D9Ej5fM());
                    void bl82 = $composer\23;
                    boolean object28 = false;
                    boolean bl87 = false;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer\77, (int)1042775818, (String)"CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo");
                    Alignment alignment3 = Alignment.Companion.getTopStart();
                    boolean bl88 = false;
                    MeasurePolicy measurePolicy6 = BoxKt.maybeCachedBoxMeasurePolicy((Alignment)alignment3, (boolean)bl88);
                    void function110 = modifier\77;
                    int measurePolicy5 = 0x70 & n34 << 3;
                    boolean bl89 = false;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer\77, (int)-1159599143, (String)"CC(Layout)P(!1,2)80@3267L27,83@3433L360:Layout.kt#80mrfh");
                    int n36 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode((Composer)$composer\77, (int)0));
                    CompositionLocalMap compositionLocalMap6 = $composer\77.getCurrentCompositionLocalMap();
                    Modifier modifier13 = ComposedModifierKt.materializeModifier((Composer)$composer\77, (Modifier)modifier\78);
                    Function0 compositionLocalMap5 = ComposeUiNode.Companion.getConstructor();
                    int modifier11 = 6 | 0x380 & n33 << 6;
                    boolean bl90 = false;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer\77, (int)-553112988, (String)"CC(ReusableComposeNode)N(factory,update,content)399@15590L9:Composables.kt#9igjgp");
                    if (!($composer\77.getApplier() instanceof Applier)) {
                        ComposablesKt.invalidApplier();
                    }
                    $composer\77.startReusableNode();
                    if ($composer\77.getInserting()) {
                        $composer\77.createNode(function019);
                    } else {
                        $composer\77.useNode();
                    }
                    Composer composer12 = Updater.constructor-impl((Composer)$composer\77);
                    boolean bl91 = false;
                    Updater.set-impl((Composer)composer12, (Object)measurePolicy6, (Function2)ComposeUiNode.Companion.getSetMeasurePolicy());
                    Updater.set-impl((Composer)composer12, (Object)compositionLocalMap6, (Function2)ComposeUiNode.Companion.getSetResolvedCompositionLocals());
                    Function2 function26 = ComposeUiNode.Companion.getSetCompositeKeyHash();
                    boolean bl92 = false;
                    Composer composer13 = composer12;
                    boolean bl93 = false;
                    if (composer13.getInserting() || !Intrinsics.areEqual((Object)composer13.rememberedValue(), (Object)n36)) {
                        composer13.updateRememberedValue((Object)n36);
                        composer12.apply((Object)n36, function26);
                    }
                    Updater.set-impl((Composer)composer12, (Object)modifier13, (Function2)ComposeUiNode.Companion.getSetModifier());
                    int composer11 = 0xE & n32 >> 6;
                    void $composer\83 = $composer\77;
                    boolean bl94 = false;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer\83, (int)1833054614, (String)"C72@3469L9:Box.kt#2w3rfo");
                    int n37 = 6 | 0x70 & n34 >> 6;
                    void bl75 = $composer\83;
                    BoxScope boxScope3 = (BoxScope)BoxScopeInstance.INSTANCE;
                    boolean bl95 = false;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer\84, (int)-773252802, (String)"C642@29676L31,643@29759L357,640@29515L802,652@30354L476,666@31375L549,659@30867L1256:GroupActivity.kt#83vr7l");
                    int $this$GroupContent_u24lambda_u24259_u24lambda_u24258_u24lambda_u24257_u24lambda_u24188_u24lambda_u24155\7322 = R.drawable.ic_sheet_close;
                    String bl76 = StringResources_androidKt.stringResource((int)R.string.cancel, (Composer)$composer\84, (int)0);
                    float invalid\4022 = spec.getTopActionButtonSize-D9Ej5fM();
                    Modifier bl44 = boxScope3.align((Modifier)Modifier.Companion, Alignment.Companion.getCenterStart());
                    int n38 = $this$GroupContent_u24lambda_u24259_u24lambda_u24258_u24lambda_u24257_u24lambda_u24188_u24lambda_u24155\7322;
                    String string4 = bl76;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer\84, (int)-163485414, (String)"CC(remember):GroupActivity.kt#9igjgp");
                    void object24 = $composer\84;
                    boolean invalid\862 = false;
                    boolean bl96 = false;
                    Object object31 = fontWeight.rememberedValue();
                    boolean bl97 = false;
                    if (object31 == Composer.Companion.getEmpty()) {
                        string3 = string4;
                        n35 = n38;
                        boolean bl98 = false;
                        function020 = () -> GroupActivity.GroupContent$lambda$259$lambda$258$lambda$257$lambda$188$lambda$168$lambda$164$lambda$163($albumSelectionModeActive$delegate, $selectedAlbumRecordIds$delegate, $showRemoveFromAlbumConfirm$delegate, $showDeleteSelectedConfirm$delegate);
                        n38 = n35;
                        string4 = string3;
                        Function0 function021 = function020;
                        fontWeight.updateRememberedValue((Object)function021);
                        object30 = function021;
                    } else {
                        object30 = object31;
                    }
                    Function0 function022 = (Function0)object30;
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer\84);
                    ComposeUiKt.GlassIconCircleButton-WH-ejsw(n38, string4, (Function0<Unit>)function022, bl44, invalid\4022, null, (Composer)$composer\84, 384, 32);
                    Object[] invalid\4022 = new Object[]{$selectedAlbumRecords.size()};
                    String string5 = this$0.getString(R.string.selected_count_format, invalid\4022);
                    Intrinsics.checkNotNullExpressionValue((Object)string5, (String)"getString(...)");
                    String $this$GroupContent_u24lambda_u24259_u24lambda_u24258_u24lambda_u24257_u24lambda_u24188_u24lambda_u24155\7322 = string5;
                    long l = $albumPalette.getTextPrimary-0d7_KjU();
                    long l3 = spec.isNarrow() ? TextUnitKt.getSp((int)20) : TextUnitKt.getSp((int)22);
                    fontWeight = FontWeight.Companion.getBold();
                    Modifier invalid\862 = boxScope3.align((Modifier)Modifier.Companion, Alignment.Companion.getCenter());
                    TextKt.Text--4IGK_g((String)$this$GroupContent_u24lambda_u24259_u24lambda_u24258_u24lambda_u24257_u24lambda_u24188_u24lambda_u24155\7322, (Modifier)invalid\862, (long)l, (long)l3, null, (FontWeight)fontWeight, null, (long)0L, null, null, (long)0L, (int)0, (boolean)false, (int)0, (int)0, null, null, (Composer)$composer\84, (int)196608, (int)0, (int)131024);
                    int $this$GroupContent_u24lambda_u24259_u24lambda_u24258_u24lambda_u24257_u24lambda_u24188_u24lambda_u24155\7322 = $allOpenedRecordsSelected ? R.drawable.ic_sheet_deselect_all : R.drawable.ic_sheet_select_all;
                    bl76 = $allOpenedRecordsSelected ? "\u53d6\u6d88\u5168\u9009" : "\u5168\u9009";
                    float invalid\4022 = spec.getTopActionButtonSize-D9Ej5fM();
                    bl44 = boxScope3.align((Modifier)Modifier.Companion, Alignment.Companion.getCenterEnd());
                    int n39 = $this$GroupContent_u24lambda_u24259_u24lambda_u24258_u24lambda_u24257_u24lambda_u24188_u24lambda_u24155\7322;
                    String string6 = bl76;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer\84, (int)-163433510, (String)"CC(remember):GroupActivity.kt#9igjgp");
                    fontWeight = $composer\84;
                    boolean bl99 = $composer\84.changed($allOpenedRecordsSelected) | $composer\84.changedInstance((Object)$openedRecords);
                    boolean bl100 = false;
                    Object object32 = $this$cache\88.rememberedValue();
                    boolean bl101 = false;
                    if (bl99 || object32 == Composer.Companion.getEmpty()) {
                        string3 = string6;
                        n35 = n39;
                        boolean bl102 = false;
                        function020 = () -> GroupActivity.GroupContent$lambda$259$lambda$258$lambda$257$lambda$188$lambda$168$lambda$167$lambda$166($allOpenedRecordsSelected, $openedRecords, $selectedAlbumRecordIds$delegate, $showRemoveFromAlbumConfirm$delegate, $showDeleteSelectedConfirm$delegate);
                        n39 = n35;
                        string6 = string3;
                        Function0 function023 = function020;
                        $this$cache\88.updateRememberedValue((Object)function023);
                        object29 = function023;
                    } else {
                        object29 = object32;
                    }
                    function022 = (Function0)object29;
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer\84);
                    ComposeUiKt.GlassIconCircleButton-WH-ejsw(n39, string6, (Function0<Unit>)function022, bl44, invalid\4022, null, (Composer)$composer\84, 0, 32);
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer\84);
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer\83);
                    $composer\77.endNode();
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer\77);
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer\77);
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer\77);
                    int n40 = 12;
                    boolean bl103 = false;
                    SpacerKt.Spacer((Modifier)SizeKt.height-3ABfNKs((Modifier)((Modifier)Modifier.Companion), (float)Dp.constructor-impl((float)n40)), (Composer)$composer\23, (int)6);
                    $composer\23.endReplaceGroup();
                } else {
                    Object object33;
                    void $this$cache\119;
                    Object object34;
                    void $this$cache\116;
                    Object object35;
                    Function0 function024;
                    int n41;
                    String string7;
                    void $this$cache\113;
                    void $composer\112;
                    void $changed\107;
                    void $changed\106;
                    void modifier\106;
                    void modifier\105;
                    void $changed\105;
                    void verticalAlignment\105;
                    void horizontalArrangement\105;
                    void $composer\105;
                    void other\104;
                    void arg0\1052;
                    void other\102;
                    float f10;
                    void modifier\94;
                    void $changed\93;
                    void modifier\93;
                    void $composer\93;
                    $composer\23.startReplaceGroup(1571394377);
                    ComposerKt.sourceInformation((Composer)$composer\23, (String)"681@32301L2975");
                    int $this$dp\932 = 12;
                    boolean bl104 = false;
                    Modifier $this$dp\932 = PaddingKt.padding-qDBjuR0$default((Modifier)SizeKt.fillMaxWidth$default((Modifier)((Modifier)Modifier.Companion), (float)0.0f, (int)1, null), (float)0.0f, (float)0.0f, (float)0.0f, (float)Dp.constructor-impl((float)$this$dp\932), (int)7, null);
                    $composer\77 = $composer\23;
                    n34 = 6;
                    boolean bl105 = false;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer\93, (int)1042775818, (String)"CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo");
                    Alignment alignment4 = Alignment.Companion.getTopStart();
                    boolean bl106 = false;
                    MeasurePolicy measurePolicy7 = BoxKt.maybeCachedBoxMeasurePolicy((Alignment)alignment4, (boolean)bl106);
                    modifier\78 = modifier\93;
                    n33 = 0x70 & $changed\93 << 3;
                    boolean bl107 = false;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer\93, (int)-1159599143, (String)"CC(Layout)P(!1,2)80@3267L27,83@3433L360:Layout.kt#80mrfh");
                    int n42 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode((Composer)$composer\93, (int)0));
                    CompositionLocalMap compositionLocalMap7 = $composer\93.getCurrentCompositionLocalMap();
                    Modifier modifier14 = ComposedModifierKt.materializeModifier((Composer)$composer\93, (Modifier)modifier\94);
                    function019 = ComposeUiNode.Companion.getConstructor();
                    n32 = 6 | 0x380 & n31 << 6;
                    boolean bl108 = false;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer\93, (int)-553112988, (String)"CC(ReusableComposeNode)N(factory,update,content)399@15590L9:Composables.kt#9igjgp");
                    if (!($composer\93.getApplier() instanceof Applier)) {
                        ComposablesKt.invalidApplier();
                    }
                    $composer\93.startReusableNode();
                    if ($composer\93.getInserting()) {
                        $composer\93.createNode(function018);
                    } else {
                        $composer\93.useNode();
                    }
                    Composer composer14 = Updater.constructor-impl((Composer)$composer\93);
                    boolean bl109 = false;
                    Updater.set-impl((Composer)composer14, (Object)measurePolicy7, (Function2)ComposeUiNode.Companion.getSetMeasurePolicy());
                    Updater.set-impl((Composer)composer14, (Object)compositionLocalMap7, (Function2)ComposeUiNode.Companion.getSetResolvedCompositionLocals());
                    Function2 function27 = ComposeUiNode.Companion.getSetCompositeKeyHash();
                    boolean bl110 = false;
                    Composer composer15 = composer14;
                    boolean bl111 = false;
                    if (composer15.getInserting() || !Intrinsics.areEqual((Object)composer15.rememberedValue(), (Object)n42)) {
                        composer15.updateRememberedValue((Object)n42);
                        composer14.apply((Object)n42, function27);
                    }
                    Updater.set-impl((Composer)composer14, (Object)modifier14, (Function2)ComposeUiNode.Companion.getSetModifier());
                    int n43 = 0xE & n30 >> 6;
                    void $composer\99 = $composer\93;
                    boolean bl112 = false;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer\99, (int)1833054614, (String)"C72@3469L9:Box.kt#2w3rfo");
                    int n44 = 6 | 0x70 & $changed\93 >> 6;
                    $composer\84 = $composer\99;
                    BoxScope boxScope4 = (BoxScope)BoxScopeInstance.INSTANCE;
                    boolean bl113 = false;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer\100, (int)440183512, (String)"C686@32555L874,700@33466L1776:GroupActivity.kt#83vr7l");
                    String $this$GroupContent_u24lambda_u24259_u24lambda_u24258_u24lambda_u24257_u24lambda_u24188_u24lambda_u24155\7322 = $openedAlbum.getName();
                    long l = $albumPalette.getTextPrimary-0d7_KjU();
                    long l4 = spec.isNarrow() ? TextUnitKt.getSp((int)20) : TextUnitKt.getSp((int)22);
                    FontWeight bl99 = FontWeight.Companion.getBold();
                    int bl100 = TextOverflow.Companion.getEllipsis-gIe3tQ8();
                    float object32 = spec.getTopActionButtonSize-D9Ej5fM();
                    int $this$dp\1022 = 24;
                    boolean bl114 = false;
                    float $this$dp\1022 = Dp.constructor-impl((float)$this$dp\1022);
                    boolean bl115 = false;
                    float f11 = Dp.constructor-impl((float)(f10 + other\102));
                    f10 = spec.getTopActionButtonSize-D9Ej5fM();
                    int $this$dp\1042 = 24;
                    boolean bl116 = false;
                    float $this$dp\1042 = Dp.constructor-impl((float)$this$dp\1042);
                    boolean bl117 = false;
                    Modifier n35 = PaddingKt.padding-qDBjuR0$default((Modifier)boxScope4.align((Modifier)Modifier.Companion, Alignment.Companion.getCenter()), (float)f11, (float)0.0f, (float)Dp.constructor-impl((float)(arg0\1052 + other\104)), (float)0.0f, (int)10, null);
                    TextKt.Text--4IGK_g((String)$this$GroupContent_u24lambda_u24259_u24lambda_u24258_u24lambda_u24257_u24lambda_u24188_u24lambda_u24155\7322, (Modifier)n35, (long)l, (long)l4, null, (FontWeight)bl99, null, (long)0L, null, null, (long)0L, (int)bl100, (boolean)false, (int)1, (int)0, null, null, (Composer)$composer\100, (int)196608, (int)3120, (int)120784);
                    $this$GroupContent_u24lambda_u24259_u24lambda_u24258_u24lambda_u24257_u24lambda_u24188_u24lambda_u24155\7322 = SizeKt.fillMaxWidth$default((Modifier)((Modifier)Modifier.Companion), (float)0.0f, (int)1, null);
                    Arrangement.Horizontal bl102 = (Arrangement.Horizontal)Arrangement.INSTANCE.getSpaceBetween();
                    Alignment.Vertical vertical5 = Alignment.Companion.getCenterVertically();
                    void function023 = $composer\100;
                    int n45 = 438;
                    boolean bl118 = false;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer\105, (int)844473419, (String)"CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo");
                    MeasurePolicy measurePolicy8 = RowKt.rowMeasurePolicy((Arrangement.Horizontal)horizontalArrangement\105, (Alignment.Vertical)verticalAlignment\105, (Composer)$composer\105, (int)(0xE & $changed\105 >> 3 | 0x70 & $changed\105 >> 3));
                    n35 = modifier\105;
                    int arg0\1052 = 0x70 & $changed\105 << 3;
                    boolean bl119 = false;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer\105, (int)-1159599143, (String)"CC(Layout)P(!1,2)80@3267L27,83@3433L360:Layout.kt#80mrfh");
                    int n46 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode((Composer)$composer\105, (int)0));
                    CompositionLocalMap compositionLocalMap8 = $composer\105.getCurrentCompositionLocalMap();
                    Modifier modifier15 = ComposedModifierKt.materializeModifier((Composer)$composer\105, (Modifier)modifier\106);
                    Function0 function025 = ComposeUiNode.Companion.getConstructor();
                    int bl46 = 6 | 0x380 & $changed\106 << 6;
                    boolean bl120 = false;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer\105, (int)-553112988, (String)"CC(ReusableComposeNode)N(factory,update,content)399@15590L9:Composables.kt#9igjgp");
                    if (!($composer\105.getApplier() instanceof Applier)) {
                        ComposablesKt.invalidApplier();
                    }
                    $composer\105.startReusableNode();
                    if ($composer\105.getInserting()) {
                        void factory\107;
                        $composer\105.createNode((Function0)factory\107);
                    } else {
                        $composer\105.useNode();
                    }
                    Composer composer16 = Updater.constructor-impl((Composer)$composer\105);
                    boolean bl121 = false;
                    Updater.set-impl((Composer)composer16, (Object)measurePolicy8, (Function2)ComposeUiNode.Companion.getSetMeasurePolicy());
                    Updater.set-impl((Composer)composer16, (Object)compositionLocalMap8, (Function2)ComposeUiNode.Companion.getSetResolvedCompositionLocals());
                    Function2 function28 = ComposeUiNode.Companion.getSetCompositeKeyHash();
                    boolean bl122 = false;
                    Composer composer17 = composer16;
                    boolean bl123 = false;
                    if (composer17.getInserting() || !Intrinsics.areEqual((Object)composer17.rememberedValue(), (Object)n46)) {
                        composer17.updateRememberedValue((Object)n46);
                        composer16.apply((Object)n46, function28);
                    }
                    Updater.set-impl((Composer)composer16, (Object)modifier15, (Function2)ComposeUiNode.Companion.getSetModifier());
                    int n47 = 0xE & $changed\107 >> 6;
                    void $composer\111 = $composer\105;
                    boolean bl124 = false;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer\111, (int)1456264949, (String)"C101@5233L9:Row.kt#2w3rfo");
                    int n48 = 6 | 0x70 & $changed\105 >> 6;
                    void var203_517 = $composer\111;
                    RowScope rowScope = (RowScope)RowScopeInstance.INSTANCE;
                    boolean bl125 = false;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer\112, (int)1043492579, (String)"C707@33972L29,708@34057L380,705@33804L752,719@34762L36,720@34854L136,724@35131L31,717@34597L607:GroupActivity.kt#83vr7l");
                    int n49 = R.drawable.ic_sheet_back;
                    String string8 = StringResources_androidKt.stringResource((int)R.string.back, (Composer)$composer\112, (int)0);
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer\112, (int)1280594116, (String)"CC(remember):GroupActivity.kt#9igjgp");
                    void var206_520 = $composer\112;
                    boolean bl126 = $composer\112.changed($openedAsStandaloneDetail) | $composer\112.changed((Object)$onCloseAlbumDetail);
                    boolean bl127 = false;
                    Object object36 = $this$cache\113.rememberedValue();
                    boolean bl128 = false;
                    if (bl126 || object36 == Composer.Companion.getEmpty()) {
                        string7 = string8;
                        n41 = n49;
                        boolean bl129 = false;
                        function024 = () -> GroupActivity.GroupContent$lambda$259$lambda$258$lambda$257$lambda$188$lambda$176$lambda$175$lambda$170$lambda$169($openedAsStandaloneDetail, $onCloseAlbumDetail, $openedAlbumId$delegate);
                        n49 = n41;
                        string8 = string7;
                        Function0 function026 = function024;
                        $this$cache\113.updateRememberedValue((Object)function026);
                        object35 = function026;
                    } else {
                        object35 = object36;
                    }
                    Function0 function027 = (Function0)object35;
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer\112);
                    ComposeUiKt.GlassIconCircleButton-WH-ejsw(n49, string8, (Function0<Unit>)function027, null, spec.getTopActionButtonSize-D9Ej5fM(), null, (Composer)$composer\112, 0, 40);
                    int n50 = R.drawable.ic_nm_more;
                    String string9 = StringResources_androidKt.stringResource((int)R.string.action_more, (Composer)$composer\112, (int)0);
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer\112, (int)1280619376, (String)"CC(remember):GroupActivity.kt#9igjgp");
                    $this$cache\113 = $composer\112;
                    boolean bl130 = false;
                    boolean bl131 = false;
                    Object object37 = $this$cache\116.rememberedValue();
                    boolean bl132 = false;
                    if (object37 == Composer.Companion.getEmpty()) {
                        string7 = string9;
                        n41 = n50;
                        boolean bl133 = false;
                        function024 = () -> GroupActivity.GroupContent$lambda$259$lambda$258$lambda$257$lambda$188$lambda$176$lambda$175$lambda$172$lambda$171($detailMoreExpanded$delegate);
                        n50 = n41;
                        string9 = string7;
                        Function0 function028 = function024;
                        $this$cache\116.updateRememberedValue((Object)function028);
                        object34 = function028;
                    } else {
                        object34 = object37;
                    }
                    function027 = (Function0)object34;
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer\112);
                    Function0 function029 = function027;
                    Modifier modifier16 = null;
                    float f12 = spec.getTopActionButtonSize-D9Ej5fM();
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer\112, (int)1280628135, (String)"CC(remember):GroupActivity.kt#9igjgp");
                    $this$cache\116 = $composer\112;
                    boolean bl134 = false;
                    boolean bl135 = false;
                    Object object38 = $this$cache\119.rememberedValue();
                    boolean bl136 = false;
                    if (object38 == Composer.Companion.getEmpty()) {
                        float f13 = f12;
                        Modifier modifier17 = modifier16;
                        function024 = function029;
                        string7 = string9;
                        n41 = n50;
                        boolean bl137 = false;
                        Function1 function112 = arg_0 -> GroupActivity.GroupContent$lambda$259$lambda$258$lambda$257$lambda$188$lambda$176$lambda$175$lambda$174$lambda$173($detailMoreAnchorBounds$delegate, arg_0);
                        n50 = n41;
                        string9 = string7;
                        function029 = function024;
                        modifier16 = modifier17;
                        f12 = f13;
                        Function1 function113 = function112;
                        $this$cache\119.updateRememberedValue((Object)function113);
                        object33 = function113;
                    } else {
                        object33 = object38;
                    }
                    function027 = (Function1)object33;
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer\112);
                    ComposeUiKt.GlassIconCircleButton-WH-ejsw(n50, string9, (Function0<Unit>)function029, modifier16, f12, (Function1<? super IntRect, Unit>)function027, (Composer)$composer\112, 196992, 8);
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer\112);
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer\111);
                    $composer\105.endNode();
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer\105);
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer\105);
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer\105);
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer\100);
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer\99);
                    $composer\93.endNode();
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer\93);
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer\93);
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer\93);
                    $composer\23.endReplaceGroup();
                }
                if (!((Collection)$openedRecords).isEmpty()) {
                    Object object39;
                    void alignment5;
                    float f14;
                    float f15;
                    $composer\23.startReplaceGroup(1574490533);
                    ComposerKt.sourceInformation((Composer)$composer\23, (String)"743@36217L2804,731@35402L3619");
                    Modifier modifier18 = ColumnScope.weight$default((ColumnScope)columnScope, (Modifier)((Modifier)Modifier.Companion), (float)1.0f, (boolean)false, (int)2, null);
                    LazyListState lazyListState = $albumDetailListState;
                    int n512 = 12;
                    boolean bl138 = false;
                    float f16 = Dp.constructor-impl((float)n512);
                    if (GroupActivity.GroupContent$lambda$67((MutableState<Boolean>)$albumSelectionModeActive$delegate) && !((Collection)$selectedAlbumRecords).isEmpty()) {
                        void other\125;
                        float f17;
                        float n512 = spec.getPageBottomPadding-D9Ej5fM();
                        if (spec.isNarrow()) {
                            int n52 = 18;
                            boolean bl139 = false;
                            f17 = Dp.constructor-impl((float)n52);
                        } else {
                            int n53 = 22;
                            boolean bl140 = false;
                            f17 = Dp.constructor-impl((float)n53);
                        }
                        float n53 = f17;
                        boolean bl141 = false;
                        f14 = Dp.constructor-impl((float)(f15 + other\125));
                    } else {
                        void other\127;
                        void arg0\127;
                        f15 = spec.getPageBottomPadding-D9Ej5fM();
                        int $this$dp\1272 = 24;
                        boolean bl142 = false;
                        float $this$dp\1272 = Dp.constructor-impl((float)$this$dp\1272);
                        boolean bl143 = false;
                        f14 = Dp.constructor-impl((float)(arg0\127 + other\127));
                    }
                    PaddingValues paddingValues = PaddingKt.PaddingValues-a9UjIt4$default((float)0.0f, (float)f16, (float)0.0f, (float)f14, (int)5, null);
                    boolean bl144 = false;
                    int $this$dp\1292 = 12;
                    boolean $i$f$getDp\128\7442 = false;
                    Arrangement.Vertical vertical6 = (Arrangement.Vertical)Arrangement.INSTANCE.spacedBy-0680j_4(Dp.constructor-impl((float)$this$dp\1292));
                    Alignment.Horizontal horizontal5 = null;
                    FlingBehavior flingBehavior = null;
                    boolean bl145 = false;
                    OverscrollEffect overscrollEffect = null;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer\23, (int)-87731026, (String)"CC(remember):GroupActivity.kt#9igjgp");
                    void $i$f$getDp\128\7442 = $composer\23;
                    boolean bl146 = $composer\23.changedInstance((Object)$openedRecords) | $composer\23.changed((Object)$albumPalette) | $composer\23.changed((Object)$onOpenDetail) | $composer\23.changed((Object)$albumAdaptive);
                    boolean bl147 = false;
                    Object object40 = alignment5.rememberedValue();
                    boolean bl148 = false;
                    if (bl146 || object40 == Composer.Companion.getEmpty()) {
                        OverscrollEffect overscrollEffect3 = overscrollEffect;
                        boolean bl149 = bl145;
                        FlingBehavior flingBehavior3 = flingBehavior;
                        Alignment.Horizontal horizontal6 = horizontal5;
                        Arrangement.Vertical vertical7 = vertical6;
                        boolean bl150 = bl144;
                        PaddingValues paddingValues3 = paddingValues;
                        LazyListState lazyListState2 = lazyListState;
                        Modifier modifier19 = modifier18;
                        boolean bl151 = false;
                        Function1 function114 = arg_0 -> GroupActivity.GroupContent$lambda$259$lambda$258$lambda$257$lambda$188$lambda$182$lambda$181($openedRecords, $albumPalette, $onOpenDetail, $albumAdaptive, $selectedAlbumRecordIds$delegate, $albumSelectionModeActive$delegate, $showRemoveFromAlbumConfirm$delegate, $showDeleteSelectedConfirm$delegate, $detailMoreExpanded$delegate, arg_0);
                        modifier18 = modifier19;
                        lazyListState = lazyListState2;
                        paddingValues = paddingValues3;
                        bl144 = bl150;
                        vertical6 = vertical7;
                        horizontal5 = horizontal6;
                        flingBehavior = flingBehavior3;
                        bl145 = bl149;
                        overscrollEffect = overscrollEffect3;
                        function111 = function114;
                        alignment5.updateRememberedValue((Object)function111);
                        object39 = function111;
                    } else {
                        object39 = object40;
                    }
                    Function1 $this$dp\1292 = (Function1)object39;
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer\23);
                    LazyDslKt.LazyColumn((Modifier)modifier18, (LazyListState)lazyListState, (PaddingValues)paddingValues, (boolean)bl144, (Arrangement.Vertical)vertical6, horizontal5, flingBehavior, (boolean)bl145, overscrollEffect, (Function1)$this$dp\1292, (Composer)$composer\23, (int)24576, (int)488);
                    $composer\23.endReplaceGroup();
                } else {
                    $composer\23.startReplaceGroup(1539290312);
                    $composer\23.endReplaceGroup();
                }
                if ($openedRecords.isEmpty()) {
                    Object object41;
                    void $this$cache\143;
                    Object object42;
                    boolean bl152;
                    void $this$cache\140;
                    void $composer\139;
                    void $changed\134;
                    void $changed\133;
                    void modifier\133;
                    void $changed\132;
                    void modifier\132;
                    void contentAlignment\132;
                    void $composer\132;
                    $composer\23.startReplaceGroup(1578134645);
                    ComposerKt.sourceInformation((Composer)$composer\23, (String)"786@39115L1515");
                    Modifier $this$dp\1292 = ColumnScope.weight$default((ColumnScope)columnScope, (Modifier)SizeKt.fillMaxWidth$default((Modifier)((Modifier)Modifier.Companion), (float)0.0f, (int)1, null), (float)1.0f, (boolean)false, (int)2, null);
                    Alignment alignment5 = Alignment.Companion.getCenter();
                    void bl147 = $composer\23;
                    int object40 = 48;
                    boolean bl153 = false;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer\132, (int)1042775818, (String)"CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo");
                    boolean bl154 = false;
                    MeasurePolicy measurePolicy9 = BoxKt.maybeCachedBoxMeasurePolicy((Alignment)contentAlignment\132, (boolean)bl154);
                    function111 = modifier\132;
                    n31 = 0x70 & $changed\132 << 3;
                    boolean bl155 = false;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer\132, (int)-1159599143, (String)"CC(Layout)P(!1,2)80@3267L27,83@3433L360:Layout.kt#80mrfh");
                    int n54 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode((Composer)$composer\132, (int)0));
                    CompositionLocalMap compositionLocalMap9 = $composer\132.getCurrentCompositionLocalMap();
                    Modifier modifier20 = ComposedModifierKt.materializeModifier((Composer)$composer\132, (Modifier)modifier\133);
                    function018 = ComposeUiNode.Companion.getConstructor();
                    n30 = 6 | 0x380 & $changed\133 << 6;
                    boolean bl156 = false;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer\132, (int)-553112988, (String)"CC(ReusableComposeNode)N(factory,update,content)399@15590L9:Composables.kt#9igjgp");
                    if (!($composer\132.getApplier() instanceof Applier)) {
                        ComposablesKt.invalidApplier();
                    }
                    $composer\132.startReusableNode();
                    if ($composer\132.getInserting()) {
                        void factory\134;
                        $composer\132.createNode((Function0)factory\134);
                    } else {
                        $composer\132.useNode();
                    }
                    Composer composer18 = Updater.constructor-impl((Composer)$composer\132);
                    boolean bl157 = false;
                    Updater.set-impl((Composer)composer18, (Object)measurePolicy9, (Function2)ComposeUiNode.Companion.getSetMeasurePolicy());
                    Updater.set-impl((Composer)composer18, (Object)compositionLocalMap9, (Function2)ComposeUiNode.Companion.getSetResolvedCompositionLocals());
                    Function2 function29 = ComposeUiNode.Companion.getSetCompositeKeyHash();
                    boolean bl158 = false;
                    Composer composer19 = composer18;
                    boolean bl159 = false;
                    if (composer19.getInserting() || !Intrinsics.areEqual((Object)composer19.rememberedValue(), (Object)n54)) {
                        composer19.updateRememberedValue((Object)n54);
                        composer18.apply((Object)n54, function29);
                    }
                    Updater.set-impl((Composer)composer18, (Object)modifier20, (Function2)ComposeUiNode.Companion.getSetModifier());
                    int n55 = 0xE & $changed\134 >> 6;
                    void $composer\138 = $composer\132;
                    boolean bl160 = false;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer\138, (int)1833054614, (String)"C72@3469L9:Box.kt#2w3rfo");
                    int n56 = 6 | 0x70 & $changed\132 >> 6;
                    $composer\100 = $composer\138;
                    BoxScope boxScope5 = (BoxScope)BoxScopeInstance.INSTANCE;
                    boolean bl161 = false;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer\139, (int)-2107267789, (String)"C794@39428L158,799@39618L996,792@39261L1363:GroupActivity.kt#83vr7l");
                    GroupActivity groupActivity = this$0;
                    boolean bl162 = Intrinsics.areEqual((Object)$openedAlbum.getOrganizeStatus(), (Object)"processing");
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer\139, (int)347669755, (String)"CC(remember):GroupActivity.kt#9igjgp");
                    void modifier\105 = $composer\139;
                    boolean bl163 = false;
                    boolean bl164 = false;
                    Object object43 = $this$cache\140.rememberedValue();
                    boolean bl165 = false;
                    if (object43 == Composer.Companion.getEmpty()) {
                        bl152 = bl162;
                        GroupActivity groupActivity2 = groupActivity;
                        boolean bl166 = false;
                        Function0 function030 = () -> GroupActivity.GroupContent$lambda$259$lambda$258$lambda$257$lambda$188$lambda$187$lambda$184$lambda$183($selectedExistingRecordIds$delegate, $addExistingSearchQuery$delegate, $showAddExistingSheet$delegate);
                        groupActivity = groupActivity2;
                        bl162 = bl152;
                        Function0 function031 = function030;
                        $this$cache\140.updateRememberedValue((Object)function031);
                        object42 = function031;
                    } else {
                        object42 = object43;
                    }
                    Function0 function032 = (Function0)object42;
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer\139);
                    Function0 function033 = function032;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer\139, (int)347676673, (String)"CC(remember):GroupActivity.kt#9igjgp");
                    $this$cache\140 = $composer\139;
                    boolean bl167 = $composer\139.changedInstance((Object)$openedAlbum) | $composer\139.changedInstance((Object)$albumContext) | $composer\139.changedInstance((Object)$settingsStore) | $composer\139.changedInstance((Object)$albumStore);
                    boolean bl168 = false;
                    Object object44 = $this$cache\143.rememberedValue();
                    boolean bl169 = false;
                    if (bl167 || object44 == Composer.Companion.getEmpty()) {
                        Function0 function034 = function033;
                        bl152 = bl162;
                        GroupActivity groupActivity3 = groupActivity;
                        boolean bl170 = false;
                        Function0 function035 = () -> GroupActivity.GroupContent$lambda$259$lambda$258$lambda$257$lambda$188$lambda$187$lambda$186$lambda$185($openedAlbum, $albumContext, $settingsStore, $albumStore, $albumList$delegate);
                        groupActivity = groupActivity3;
                        bl162 = bl152;
                        function033 = function034;
                        Function0 function036 = function035;
                        $this$cache\143.updateRememberedValue((Object)function036);
                        object41 = function036;
                    } else {
                        object41 = object44;
                    }
                    function032 = (Function0)object41;
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer\139);
                    groupActivity.GroupAlbumDetailEmptyState(bl162, (Function0<Unit>)function033, (Function0<Unit>)function032, (Composer)$composer\139, 48);
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer\139);
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer\138);
                    $composer\132.endNode();
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer\132);
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer\132);
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer\132);
                    $composer\23.endReplaceGroup();
                } else {
                    $composer\23.startReplaceGroup(1539290312);
                    $composer\23.endReplaceGroup();
                }
                $composer\23.endReplaceGroup();
            } else {
                $composer\23.startReplaceGroup(1579632906);
                ComposerKt.sourceInformation((Composer)$composer\23, (String)"821@40694L38");
                SpacerKt.Spacer((Modifier)ColumnScope.weight$default((ColumnScope)columnScope, (Modifier)((Modifier)Modifier.Companion), (float)1.0f, (boolean)false, (int)2, null), (Composer)$composer\23, (int)0);
                $composer\23.endReplaceGroup();
            }
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\23);
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\22);
            $composer\16.endNode();
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\16);
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\16);
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\16);
            if ($openedAlbum == null) {
                Object object45;
                void $this$cache\148;
                float f18;
                $composer\8.startReplaceGroup(-1759570250);
                ComposerKt.sourceInformation((Composer)$composer\8, (String)"829@41032L2,826@40853L1036");
                function1 = NoMemoDockTab.GROUP;
                Modifier modifier21 = WindowInsetsPadding_androidKt.navigationBarsPadding((Modifier)boxScope.align((Modifier)Modifier.Companion, Alignment.Companion.getBottomCenter()));
                float f19 = spec.getPageHorizontalPadding-D9Ej5fM();
                float f20 = spec.getPageHorizontalPadding-D9Ej5fM();
                if (spec.isNarrow()) {
                    int n57 = 10;
                    boolean bl171 = false;
                    f18 = Dp.constructor-impl((float)n57);
                } else {
                    int n58 = 14;
                    boolean bl172 = false;
                    f18 = Dp.constructor-impl((float)n58);
                }
                vertical = PaddingKt.padding-qDBjuR0$default((Modifier)modifier21, (float)f19, (float)0.0f, (float)f20, (float)f18, (int)2, null);
                Object object46 = function1;
                Function0 function037 = $onOpenMemory;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\8, (int)-1442228174, (String)"CC(remember):GroupActivity.kt#9igjgp");
                void bl172 = $composer\8;
                boolean bl173 = false;
                boolean bl174 = false;
                Object object47 = $this$cache\148.rememberedValue();
                boolean bl175 = false;
                if (object47 == Composer.Companion.getEmpty()) {
                    object13 = function037;
                    object14 = object46;
                    boolean bl176 = false;
                    object11 = GroupActivity::GroupContent$lambda$259$lambda$258$lambda$257$lambda$190$lambda$189;
                    object46 = object14;
                    function037 = object13;
                    Function0 function038 = object11;
                    $this$cache\148.updateRememberedValue((Object)function038);
                    object45 = function038;
                } else {
                    object45 = object47;
                }
                Function0 n58 = (Function0)object45;
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\8);
                LiquidGlassDockKt.LiquidGlassDock(object46, (Function0<Unit>)function037, (Function0<Unit>)n58, (Function0<Unit>)$onOpenReminder, (Function0<Unit>)$onAddClick, false, (Modifier)vertical, spec, (Backdrop)layerBackdrop, null, false, $startupDockPulseTab, 140L, (Composer)$composer\8, 0x186 | 0x1C00000 & $dirty << 18, 384, 1568);
                $composer\8.endReplaceGroup();
            } else if (GroupActivity.GroupContent$lambda$67((MutableState<Boolean>)$albumSelectionModeActive$delegate) && !((Collection)$selectedAlbumRecords).isEmpty()) {
                Object object48;
                void $this$cache\156;
                Object object49;
                void $this$cache\153;
                float f21;
                $composer\8.startReplaceGroup(-1758424614);
                ComposerKt.sourceInformation((Composer)$composer\8, (String)"850@42316L37,851@42399L36,846@42010L968");
                function1 = $allOpenedRecordsSelected ? "\u5168\u90e8\u79fb\u51fa" : "\u79fb\u51fa";
                Modifier modifier22 = WindowInsetsPadding_androidKt.navigationBarsPadding((Modifier)boxScope.align((Modifier)Modifier.Companion, Alignment.Companion.getBottomCenter()));
                float f22 = spec.getPageHorizontalPadding-D9Ej5fM();
                float f23 = spec.getPageHorizontalPadding-D9Ej5fM();
                if (spec.isNarrow()) {
                    int n59 = 18;
                    boolean bl177 = false;
                    f21 = Dp.constructor-impl((float)n59);
                } else {
                    int n60 = 22;
                    boolean bl178 = false;
                    f21 = Dp.constructor-impl((float)n60);
                }
                vertical = PaddingKt.padding-qDBjuR0$default((Modifier)modifier22, (float)f22, (float)0.0f, (float)f23, (float)f21, (int)2, null);
                Object object50 = $selectedAlbumRecords;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\8, (int)-1442187051, (String)"CC(remember):GroupActivity.kt#9igjgp");
                void bl178 = $composer\8;
                boolean bl179 = false;
                boolean bl180 = false;
                Object object51 = $this$cache\153.rememberedValue();
                boolean bl181 = false;
                if (object51 == Composer.Companion.getEmpty()) {
                    object14 = object50;
                    boolean bl182 = false;
                    object13 = () -> GroupActivity.GroupContent$lambda$259$lambda$258$lambda$257$lambda$192$lambda$191($showRemoveFromAlbumConfirm$delegate);
                    object50 = object14;
                    Object object52 = object13;
                    $this$cache\153.updateRememberedValue(object52);
                    object49 = object52;
                } else {
                    object49 = object51;
                }
                Function0 n60 = (Function0)object49;
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\8);
                Function0 function039 = n60;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\8, (int)-1442184396, (String)"CC(remember):GroupActivity.kt#9igjgp");
                $this$cache\153 = $composer\8;
                boolean bl183 = false;
                boolean bl184 = false;
                Object object53 = $this$cache\156.rememberedValue();
                boolean bl185 = false;
                if (object53 == Composer.Companion.getEmpty()) {
                    object13 = function039;
                    object14 = object50;
                    boolean bl186 = false;
                    object11 = () -> GroupActivity.GroupContent$lambda$259$lambda$258$lambda$257$lambda$194$lambda$193($showDeleteSelectedConfirm$delegate);
                    object50 = object14;
                    function039 = object13;
                    Function0 function040 = object11;
                    $this$cache\156.updateRememberedValue((Object)function040);
                    object48 = function040;
                } else {
                    object48 = object53;
                }
                n60 = (Function0)object48;
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\8);
                SelectionUiKt.NoMemoSelectionActionDock(object50, (Function0<Unit>)function039, (Function0<Unit>)n60, $allOpenedRecordsSelected, false, (String)function1, (Backdrop)layerBackdrop, (Modifier)vertical, (Composer)$composer\8, 432, 16);
                $composer\8.endReplaceGroup();
            } else {
                $composer\8.startReplaceGroup(-1800105230);
                $composer\8.endReplaceGroup();
            }
            boolean bl187 = $openedAlbum == null && GroupActivity.GroupContent$lambda$52((MutableState<Boolean>)$groupListMoreExpanded$delegate);
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\8, (int)-1442159855, (String)"CC(remember):GroupActivity.kt#9igjgp");
            vertical = $composer\8;
            boolean invalid\1602 = false;
            boolean bl188 = false;
            Object object54 = function06.rememberedValue();
            boolean bl189 = false;
            if (object54 == Composer.Companion.getEmpty()) {
                boolean bl190 = bl187;
                boolean bl191 = false;
                object13 = () -> GroupActivity.GroupContent$lambda$259$lambda$258$lambda$257$lambda$196$lambda$195($groupListMoreExpanded$delegate);
                bl187 = bl190;
                Function1 function115 = object13;
                function06.updateRememberedValue((Object)function115);
                object10 = function115;
            } else {
                object10 = object54;
            }
            function1 = (Function0)object10;
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\8);
            Object object55 = function1;
            IntRect intRect = GroupActivity.GroupContent$lambda$58((MutableState<IntRect>)$groupListMoreAnchorBounds$delegate);
            Function1 function116 = function1 = new NoMemoMenuActionItem[3];
            int n61 = 0;
            int n62 = R.drawable.ic_nm_add;
            String string10 = "\u65b0\u589e\u5206\u7ec4";
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\8, (int)-1442148618, (String)"CC(remember):GroupActivity.kt#9igjgp");
            void invalid\1602 = $composer\8;
            boolean bl192 = false;
            boolean bl193 = false;
            Object object56 = $this$cache\162.rememberedValue();
            boolean bl194 = false;
            if (object56 == Composer.Companion.getEmpty()) {
                string2 = string10;
                n2 = n62;
                n = n61;
                object9 = function116;
                object11 = intRect;
                object13 = object55;
                boolean bl195 = bl187;
                boolean bl196 = false;
                function05 = () -> GroupActivity.GroupContent$lambda$259$lambda$258$lambda$257$lambda$198$lambda$197($groupListMoreExpanded$delegate, $showCreateAlbumDialog$delegate);
                bl187 = bl195;
                object55 = object13;
                intRect = object11;
                function116 = object9;
                n61 = n;
                n62 = n2;
                string10 = string2;
                Function0 function041 = function05;
                $this$cache\162.updateRememberedValue((Object)function041);
                object8 = function041;
            } else {
                object8 = object56;
            }
            function06 = (Function0)object8;
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\8);
            DefaultConstructorMarker defaultConstructorMarker = null;
            int n63 = 8;
            boolean bl197 = false;
            Function0 function042 = function06;
            String string11 = string10;
            int n64 = n62;
            function116[n61] = new NoMemoMenuActionItem(n64, string11, function042, bl197, n63, defaultConstructorMarker);
            Function1 function117 = function1;
            int n65 = 1;
            int n66 = R.drawable.ic_nm_memory;
            String string12 = "\u5df2\u5f52\u6863\u8bb0\u5fc6";
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\8, (int)-1442135590, (String)"CC(remember):GroupActivity.kt#9igjgp");
            $this$cache\162 = $composer\8;
            boolean bl198 = $composer\8.changedInstance((Object)this$0) | $composer\8.changedInstance((Object)$albumContext);
            boolean bl199 = false;
            Object object57 = $this$cache\165.rememberedValue();
            boolean bl200 = false;
            if (bl198 || object57 == Composer.Companion.getEmpty()) {
                string2 = string12;
                n2 = n66;
                n = n65;
                object9 = function117;
                object11 = intRect;
                object13 = object55;
                boolean bl201 = bl187;
                boolean bl202 = false;
                function05 = () -> GroupActivity.GroupContent$lambda$259$lambda$258$lambda$257$lambda$200$lambda$199(this$0, $albumContext, $groupListMoreExpanded$delegate);
                bl187 = bl201;
                object55 = object13;
                intRect = object11;
                function117 = object9;
                n65 = n;
                n66 = n2;
                string12 = string2;
                Function0 function043 = function05;
                $this$cache\165.updateRememberedValue((Object)function043);
                object7 = function043;
            } else {
                object7 = object57;
            }
            function06 = (Function0)object7;
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\8);
            DefaultConstructorMarker defaultConstructorMarker2 = null;
            int n67 = 8;
            boolean bl203 = false;
            Function0 function044 = function06;
            String string13 = string12;
            int n68 = n66;
            function117[n65] = new NoMemoMenuActionItem(n68, string13, function044, bl203, n67, defaultConstructorMarker2);
            Function1 function118 = function1;
            int n69 = 2;
            int n70 = R.drawable.ic_nm_settings;
            String string14 = StringResources_androidKt.stringResource((int)R.string.action_settings, (Composer)$composer\8, (int)0);
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\8, (int)-1442120374, (String)"CC(remember):GroupActivity.kt#9igjgp");
            $this$cache\165 = $composer\8;
            boolean bl204 = $composer\8.changed((Object)$onOpenSettings);
            boolean bl205 = false;
            Object object58 = $this$cache\168.rememberedValue();
            boolean bl206 = false;
            if (bl204 || object58 == Composer.Companion.getEmpty()) {
                string2 = string14;
                n2 = n70;
                n = n69;
                object9 = function118;
                object11 = intRect;
                object13 = object55;
                boolean bl207 = bl187;
                boolean bl208 = false;
                function05 = () -> GroupActivity.GroupContent$lambda$259$lambda$258$lambda$257$lambda$202$lambda$201($onOpenSettings, $groupListMoreExpanded$delegate);
                bl187 = bl207;
                object55 = object13;
                intRect = object11;
                function118 = object9;
                n69 = n;
                n70 = n2;
                string14 = string2;
                Function0 function045 = function05;
                $this$cache\168.updateRememberedValue((Object)function045);
                object6 = function045;
            } else {
                object6 = object58;
            }
            function06 = (Function0)object6;
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\8);
            DefaultConstructorMarker defaultConstructorMarker3 = null;
            int n71 = 8;
            boolean bl209 = false;
            Function0 function046 = function06;
            String string15 = string14;
            int n72 = n70;
            function118[n69] = new NoMemoMenuActionItem(n72, string15, function046, bl209, n71, defaultConstructorMarker3);
            ComposeUiKt.NoMemoAnchoredMenu-AGcomas(bl187, (Function0<Unit>)object55, intRect, CollectionsKt.listOf((Object[])function1), 0.0f, 0.0f, (Composer)$composer\8, 48, 48);
            boolean bl210 = $openedAlbum != null && GroupActivity.GroupContent$lambda$55((MutableState<Boolean>)$detailMoreExpanded$delegate);
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\8, (int)-1442107858, (String)"CC(remember):GroupActivity.kt#9igjgp");
            function06 = $composer\8;
            boolean invalid\1722 = false;
            boolean bl211 = false;
            Object object59 = function043.rememberedValue();
            boolean bl212 = false;
            if (object59 == Composer.Companion.getEmpty()) {
                boolean bl213 = bl210;
                boolean bl214 = false;
                object13 = () -> GroupActivity.GroupContent$lambda$259$lambda$258$lambda$257$lambda$204$lambda$203($detailMoreExpanded$delegate);
                bl210 = bl213;
                Object object60 = object13;
                function043.updateRememberedValue(object60);
                object5 = object60;
            } else {
                object5 = object59;
            }
            function1 = (Function1)object5;
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\8);
            Function1 function119 = function1;
            IntRect intRect2 = GroupActivity.GroupContent$lambda$61((MutableState<IntRect>)$detailMoreAnchorBounds$delegate);
            Function1 function120 = function1 = new NoMemoMenuActionItem[4];
            int n73 = 0;
            int n74 = R.drawable.ic_sheet_select_all;
            String string16 = "\u5168\u9009";
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\8, (int)-1442096310, (String)"CC(remember):GroupActivity.kt#9igjgp");
            void invalid\1722 = $composer\8;
            boolean bl215 = $composer\8.changedInstance((Object)$openedRecords);
            boolean bl216 = false;
            Object object61 = $this$cache\174.rememberedValue();
            boolean bl217 = false;
            if (bl215 || object61 == Composer.Companion.getEmpty()) {
                string2 = string16;
                n2 = n74;
                n = n73;
                object9 = function120;
                object11 = intRect2;
                object13 = function119;
                boolean bl218 = bl210;
                boolean bl219 = false;
                function05 = () -> GroupActivity.GroupContent$lambda$259$lambda$258$lambda$257$lambda$207$lambda$206($openedRecords, $detailMoreExpanded$delegate, $albumSelectionModeActive$delegate, $selectedAlbumRecordIds$delegate, $showRemoveFromAlbumConfirm$delegate, $showDeleteSelectedConfirm$delegate);
                bl210 = bl218;
                function119 = object13;
                intRect2 = object11;
                function120 = object9;
                n73 = n;
                n74 = n2;
                string16 = string2;
                Function0 function047 = function05;
                $this$cache\174.updateRememberedValue((Object)function047);
                object4 = function047;
            } else {
                object4 = object61;
            }
            function043 = (Function0)object4;
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\8);
            DefaultConstructorMarker defaultConstructorMarker4 = null;
            int n75 = 8;
            boolean bl220 = false;
            Function0 function048 = function043;
            String string17 = string16;
            int n76 = n74;
            function120[n73] = new NoMemoMenuActionItem(n76, string17, function048, bl220, n75, defaultConstructorMarker4);
            Function1 function121 = function1;
            int n77 = 1;
            int n78 = R.drawable.ic_nm_add;
            String string18 = "\u6dfb\u52a0\u8bb0\u5fc6";
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\8, (int)-1442075747, (String)"CC(remember):GroupActivity.kt#9igjgp");
            $this$cache\174 = $composer\8;
            boolean bl221 = false;
            boolean bl222 = false;
            Object object62 = $this$cache\177.rememberedValue();
            boolean bl223 = false;
            if (object62 == Composer.Companion.getEmpty()) {
                string2 = string18;
                n2 = n78;
                n = n77;
                object9 = function121;
                object11 = intRect2;
                object13 = function119;
                boolean bl224 = bl210;
                boolean bl225 = false;
                function05 = () -> GroupActivity.GroupContent$lambda$259$lambda$258$lambda$257$lambda$209$lambda$208($detailMoreExpanded$delegate, $selectedExistingRecordIds$delegate, $addExistingSearchQuery$delegate, $showAddExistingSheet$delegate);
                bl210 = bl224;
                function119 = object13;
                intRect2 = object11;
                function121 = object9;
                n77 = n;
                n78 = n2;
                string18 = string2;
                Function0 function049 = function05;
                $this$cache\177.updateRememberedValue((Object)function049);
                object3 = function049;
            } else {
                object3 = object62;
            }
            function043 = (Function0)object3;
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\8);
            DefaultConstructorMarker defaultConstructorMarker5 = null;
            int n79 = 8;
            boolean bl226 = false;
            Function0 function050 = function043;
            String string19 = string18;
            int n80 = n78;
            function121[n77] = new NoMemoMenuActionItem(n80, string19, function050, bl226, n79, defaultConstructorMarker5);
            Function1 function122 = function1;
            int n81 = 2;
            int n82 = R.drawable.ic_nm_edit;
            String string20 = "\u7f16\u8f91\u5206\u7ec4";
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\8, (int)-1442058344, (String)"CC(remember):GroupActivity.kt#9igjgp");
            $this$cache\177 = $composer\8;
            boolean invalid\1812 = $composer\8.changedInstance((Object)$openedAlbum);
            boolean bl227 = false;
            Object object63 = function03.rememberedValue();
            boolean bl228 = false;
            if (invalid\1812 || object63 == Composer.Companion.getEmpty()) {
                string2 = string20;
                n2 = n82;
                n = n81;
                object9 = function122;
                object11 = intRect2;
                object13 = function119;
                boolean bl229 = bl210;
                boolean bl230 = false;
                function05 = () -> GroupActivity.GroupContent$lambda$259$lambda$258$lambda$257$lambda$212$lambda$211($openedAlbum, $detailMoreExpanded$delegate, $editingAlbumId$delegate, $albumNameInput$delegate, $albumDescriptionInput$delegate, $showEditAlbumDialog$delegate);
                bl210 = bl229;
                function119 = object13;
                intRect2 = object11;
                function122 = object9;
                n81 = n;
                n82 = n2;
                string20 = string2;
                Function0 function051 = function05;
                function03.updateRememberedValue((Object)function051);
                object2 = function051;
            } else {
                object2 = object63;
            }
            function043 = (Function0)object2;
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\8);
            DefaultConstructorMarker defaultConstructorMarker6 = null;
            int n83 = 8;
            boolean bl231 = false;
            Function0 function052 = function043;
            String string21 = string20;
            int n84 = n82;
            function122[n81] = new NoMemoMenuActionItem(n84, string21, function052, bl231, n83, defaultConstructorMarker6);
            Function1 function123 = function1;
            int n85 = 3;
            int n86 = function043 = R.drawable.ic_nm_delete;
            String string22 = "\u5220\u9664\u5206\u7ec4";
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\8, (int)-1442033740, (String)"CC(remember):GroupActivity.kt#9igjgp");
            void invalid\1812 = $composer\8;
            boolean bl232 = false;
            boolean bl233 = false;
            Object object64 = $this$cache\183.rememberedValue();
            boolean bl234 = false;
            if (object64 == Composer.Companion.getEmpty()) {
                string2 = string22;
                n2 = n86;
                n = n85;
                object9 = function123;
                object11 = intRect2;
                object13 = function119;
                boolean bl235 = bl210;
                boolean bl236 = false;
                function05 = () -> GroupActivity.GroupContent$lambda$259$lambda$258$lambda$257$lambda$214$lambda$213($detailMoreExpanded$delegate, $showDeleteAlbumConfirm$delegate);
                bl210 = bl235;
                function119 = object13;
                intRect2 = object11;
                function123 = object9;
                n85 = n;
                n86 = n2;
                string22 = string2;
                Function0 function053 = function05;
                $this$cache\183.updateRememberedValue((Object)function053);
                object = function053;
            } else {
                object = object64;
            }
            function03 = (Function0)object;
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\8);
            boolean bl237 = true;
            Function0 function054 = function03;
            String string23 = string22;
            int n87 = n86;
            function123[n85] = new NoMemoMenuActionItem(n87, string23, (Function0<Unit>)function054, bl237);
            ComposeUiKt.NoMemoAnchoredMenu-AGcomas(bl210, (Function0<Unit>)function119, intRect2, CollectionsKt.listOf((Object[])function1), 0.0f, 0.0f, (Composer)$composer\8, 48, 48);
            if (GroupActivity.GroupContent$lambda$40((MutableState<Boolean>)$showCreateAlbumDialog$delegate)) {
                Object object65;
                void $this$cache\198;
                Object object66;
                void $this$cache\195;
                Object object67;
                void $this$cache\192;
                Object object68;
                void $this$cache\189;
                Object object69;
                boolean bl238;
                boolean bl239;
                void $this$cache\186;
                $composer\8.startReplaceGroup(-1752990717);
                ComposerKt.sourceInformation((Composer)$composer\8, (String)"954@47805L38,955@47888L23,956@47963L30,957@48035L274,963@48351L2178,948@47420L3135");
                GroupActivity groupActivity = this$0;
                BoxScope boxScope6 = boxScope;
                String string24 = "\u65b0\u5efa\u5206\u7ec4";
                String string25 = GroupActivity.GroupContent$lambda$88((MutableState<String>)$albumNameInput$delegate);
                String string26 = GroupActivity.GroupContent$lambda$91((MutableState<String>)$albumDescriptionInput$delegate);
                boolean bl240 = true;
                boolean bl241 = GroupActivity.GroupContent$lambda$94((MutableState<Boolean>)$albumAutoClassifyEnabledInput$delegate);
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\8, (int)-1442011402, (String)"CC(remember):GroupActivity.kt#9igjgp");
                void function043 = $composer\8;
                boolean bl242 = false;
                boolean bl243 = false;
                Object object70 = $this$cache\186.rememberedValue();
                boolean bl244 = false;
                if (object70 == Composer.Companion.getEmpty()) {
                    bl239 = bl241;
                    bl238 = bl240;
                    String string27 = string26;
                    object9 = string25;
                    object11 = string24;
                    object13 = boxScope6;
                    GroupActivity groupActivity4 = groupActivity;
                    boolean bl245 = false;
                    Function1 function124 = arg_0 -> GroupActivity.GroupContent$lambda$259$lambda$258$lambda$257$lambda$216$lambda$215($albumAutoClassifyEnabledInput$delegate, arg_0);
                    groupActivity = groupActivity4;
                    boxScope6 = object13;
                    string24 = object11;
                    string25 = object9;
                    string26 = string27;
                    bl240 = bl238;
                    bl241 = bl239;
                    Function1 function125 = function124;
                    $this$cache\186.updateRememberedValue((Object)function125);
                    object69 = function125;
                } else {
                    object69 = object70;
                }
                function1 = (Function1)object69;
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\8);
                Function1 function126 = function1;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\8, (int)-1442008761, (String)"CC(remember):GroupActivity.kt#9igjgp");
                $this$cache\186 = $composer\8;
                boolean bl246 = false;
                boolean bl247 = false;
                Object object71 = $this$cache\189.rememberedValue();
                boolean bl248 = false;
                if (object71 == Composer.Companion.getEmpty()) {
                    Function1 function127 = function126;
                    bl239 = bl241;
                    bl238 = bl240;
                    String string28 = string26;
                    object9 = string25;
                    object11 = string24;
                    object13 = boxScope6;
                    GroupActivity groupActivity5 = groupActivity;
                    boolean bl249 = false;
                    string2 = arg_0 -> GroupActivity.GroupContent$lambda$259$lambda$258$lambda$257$lambda$218$lambda$217($albumNameInput$delegate, arg_0);
                    groupActivity = groupActivity5;
                    boxScope6 = object13;
                    string24 = object11;
                    string25 = object9;
                    string26 = string28;
                    bl240 = bl238;
                    bl241 = bl239;
                    function126 = function127;
                    String string29 = string2;
                    $this$cache\189.updateRememberedValue((Object)string29);
                    object68 = string29;
                } else {
                    object68 = object71;
                }
                function1 = (Function1)object68;
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\8);
                Object object72 = function1;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\8, (int)-1442006354, (String)"CC(remember):GroupActivity.kt#9igjgp");
                $this$cache\189 = $composer\8;
                boolean bl250 = false;
                boolean bl251 = false;
                Object object73 = $this$cache\192.rememberedValue();
                boolean bl252 = false;
                if (object73 == Composer.Companion.getEmpty()) {
                    string2 = object72;
                    Function1 function128 = function126;
                    bl239 = bl241;
                    bl238 = bl240;
                    String string30 = string26;
                    object9 = string25;
                    object11 = string24;
                    object13 = boxScope6;
                    GroupActivity groupActivity6 = groupActivity;
                    boolean bl253 = false;
                    function05 = arg_0 -> GroupActivity.GroupContent$lambda$259$lambda$258$lambda$257$lambda$220$lambda$219($albumDescriptionInput$delegate, arg_0);
                    groupActivity = groupActivity6;
                    boxScope6 = object13;
                    string24 = object11;
                    string25 = object9;
                    string26 = string30;
                    bl240 = bl238;
                    bl241 = bl239;
                    function126 = function128;
                    object72 = string2;
                    Function0 function055 = function05;
                    $this$cache\192.updateRememberedValue((Object)function055);
                    object67 = function055;
                } else {
                    object67 = object73;
                }
                function1 = (Function1)object67;
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\8);
                Function1 function129 = function1;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\8, (int)-1442003806, (String)"CC(remember):GroupActivity.kt#9igjgp");
                $this$cache\192 = $composer\8;
                boolean bl254 = false;
                boolean bl255 = false;
                Object object74 = $this$cache\195.rememberedValue();
                boolean bl256 = false;
                if (object74 == Composer.Companion.getEmpty()) {
                    function05 = function129;
                    string2 = object72;
                    Function1 function130 = function126;
                    bl239 = bl241;
                    bl238 = bl240;
                    String string31 = string26;
                    object9 = string25;
                    object11 = string24;
                    object13 = boxScope6;
                    GroupActivity groupActivity7 = groupActivity;
                    boolean bl257 = false;
                    function02 = () -> GroupActivity.GroupContent$lambda$259$lambda$258$lambda$257$lambda$222$lambda$221($showCreateAlbumDialog$delegate, $albumNameInput$delegate, $albumDescriptionInput$delegate, $albumAutoClassifyEnabledInput$delegate);
                    groupActivity = groupActivity7;
                    boxScope6 = object13;
                    string24 = object11;
                    string25 = object9;
                    string26 = string31;
                    bl240 = bl238;
                    bl241 = bl239;
                    function126 = function130;
                    object72 = string2;
                    function129 = function05;
                    Function0 function056 = function02;
                    $this$cache\195.updateRememberedValue((Object)function056);
                    object66 = function056;
                } else {
                    object66 = object74;
                }
                function1 = (Function0)object66;
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\8);
                Function1 function131 = function1;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\8, (int)-1441991790, (String)"CC(remember):GroupActivity.kt#9igjgp");
                $this$cache\195 = $composer\8;
                boolean bl258 = $composer\8.changedInstance((Object)$albumContext) | $composer\8.changedInstance((Object)$settingsStore) | $composer\8.changedInstance((Object)$albumStore);
                boolean bl259 = false;
                Object object75 = $this$cache\198.rememberedValue();
                boolean bl260 = false;
                if (bl258 || object75 == Composer.Companion.getEmpty()) {
                    function02 = function131;
                    function05 = function129;
                    string2 = object72;
                    Function1 function132 = function126;
                    bl239 = bl241;
                    bl238 = bl240;
                    String string32 = string26;
                    object9 = string25;
                    object11 = string24;
                    object13 = boxScope6;
                    GroupActivity groupActivity8 = groupActivity;
                    boolean bl261 = false;
                    function0 = () -> GroupActivity.GroupContent$lambda$259$lambda$258$lambda$257$lambda$225$lambda$224($albumContext, $settingsStore, $albumStore, $albumNameInput$delegate, $albumAutoClassifyEnabledInput$delegate, $albumDescriptionInput$delegate, $albumList$delegate);
                    groupActivity = groupActivity8;
                    boxScope6 = object13;
                    string24 = object11;
                    string25 = object9;
                    string26 = string32;
                    bl240 = bl238;
                    bl241 = bl239;
                    function126 = function132;
                    object72 = string2;
                    function129 = function05;
                    function131 = function02;
                    Function0 function057 = function0;
                    $this$cache\198.updateRememberedValue((Object)function057);
                    object65 = function057;
                } else {
                    object65 = object75;
                }
                function1 = (Function0)object65;
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\8);
                groupActivity.GroupEditAlbumSheet(boxScope6, string24, string25, string26, bl240, bl241, (Function1<? super Boolean, Unit>)function126, (Function1<? super String, Unit>)object72, (Function1<? super String, Unit>)function129, (Function0<Unit>)function131, (Function0<Boolean>)function1, (Composer)$composer\8, 0x36D86030 | 0xE & $changed\8, 0);
                $composer\8.endReplaceGroup();
            } else {
                $composer\8.startReplaceGroup(-1800105230);
                $composer\8.endReplaceGroup();
            }
            if (GroupActivity.GroupContent$lambda$43((MutableState<Boolean>)$showAddExistingSheet$delegate) && $openedAlbum != null) {
                Object object76;
                void $this$cache\210;
                Object object77;
                void $this$cache\207;
                Object object78;
                void $this$cache\204;
                Object object79;
                void $this$cache\201;
                $composer\8.startReplaceGroup(-1749786805);
                ComposerKt.sourceInformation((Composer)$composer\8, (String)"1002@50957L31,1003@51035L376,1010@51453L223,1015@51718L1289,998@50674L2359");
                GroupActivity groupActivity = this$0;
                BoxScope boxScope7 = boxScope;
                List list = $filteredExistingRecords;
                Object object80 = GroupActivity.GroupContent$lambda$46((MutableState<Set<String>>)$selectedExistingRecordIds$delegate);
                String string33 = GroupActivity.GroupContent$lambda$49((MutableState<String>)$addExistingSearchQuery$delegate);
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\8, (int)-1441910545, (String)"CC(remember):GroupActivity.kt#9igjgp");
                void $this$cache\198 = $composer\8;
                boolean bl262 = false;
                boolean bl263 = false;
                Object object81 = $this$cache\201.rememberedValue();
                boolean bl264 = false;
                if (object81 == Composer.Companion.getEmpty()) {
                    String string34 = string33;
                    object9 = object80;
                    object11 = list;
                    object13 = boxScope7;
                    GroupActivity groupActivity9 = groupActivity;
                    boolean bl265 = false;
                    Function1 function133 = arg_0 -> GroupActivity.GroupContent$lambda$259$lambda$258$lambda$257$lambda$227$lambda$226($addExistingSearchQuery$delegate, arg_0);
                    groupActivity = groupActivity9;
                    boxScope7 = object13;
                    list = object11;
                    object80 = object9;
                    string33 = string34;
                    Function1 function134 = function133;
                    $this$cache\201.updateRememberedValue((Object)function134);
                    object79 = function134;
                } else {
                    object79 = object81;
                }
                function1 = (Function1)object79;
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\8);
                Function1 function135 = function1;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\8, (int)-1441907704, (String)"CC(remember):GroupActivity.kt#9igjgp");
                $this$cache\201 = $composer\8;
                boolean bl266 = false;
                boolean bl267 = false;
                Object object82 = $this$cache\204.rememberedValue();
                boolean bl268 = false;
                if (object82 == Composer.Companion.getEmpty()) {
                    Function1 function136 = function135;
                    String string35 = string33;
                    object9 = object80;
                    object11 = list;
                    object13 = boxScope7;
                    GroupActivity groupActivity10 = groupActivity;
                    boolean bl269 = false;
                    Function1 function137 = arg_0 -> GroupActivity.GroupContent$lambda$259$lambda$258$lambda$257$lambda$229$lambda$228($selectedExistingRecordIds$delegate, arg_0);
                    groupActivity = groupActivity10;
                    boxScope7 = object13;
                    list = object11;
                    object80 = object9;
                    string33 = string35;
                    function135 = function136;
                    Function1 function138 = function137;
                    $this$cache\204.updateRememberedValue((Object)function138);
                    object78 = function138;
                } else {
                    object78 = object82;
                }
                function1 = (Function1)object78;
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\8);
                Function1 function139 = function1;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\8, (int)-1441894481, (String)"CC(remember):GroupActivity.kt#9igjgp");
                $this$cache\204 = $composer\8;
                boolean bl270 = false;
                boolean bl271 = false;
                Object object83 = $this$cache\207.rememberedValue();
                boolean bl272 = false;
                if (object83 == Composer.Companion.getEmpty()) {
                    Function1 function140 = function139;
                    Function1 function141 = function135;
                    String string36 = string33;
                    object9 = object80;
                    object11 = list;
                    object13 = boxScope7;
                    GroupActivity groupActivity11 = groupActivity;
                    boolean bl273 = false;
                    Function0 function058 = () -> GroupActivity.GroupContent$lambda$259$lambda$258$lambda$257$lambda$231$lambda$230($showAddExistingSheet$delegate, $selectedExistingRecordIds$delegate, $addExistingSearchQuery$delegate);
                    groupActivity = groupActivity11;
                    boxScope7 = object13;
                    list = object11;
                    object80 = object9;
                    string33 = string36;
                    function135 = function141;
                    function139 = function140;
                    Function0 function059 = function058;
                    $this$cache\207.updateRememberedValue((Object)function059);
                    object77 = function059;
                } else {
                    object77 = object83;
                }
                function1 = (Function0)object77;
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\8);
                Function1 function142 = function1;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\8, (int)-1441884935, (String)"CC(remember):GroupActivity.kt#9igjgp");
                $this$cache\207 = $composer\8;
                boolean bl274 = $composer\8.changedInstance((Object)$albumContext) | $composer\8.changedInstance((Object)$albumStore);
                boolean bl275 = false;
                Object object84 = $this$cache\210.rememberedValue();
                boolean bl276 = false;
                if (bl274 || object84 == Composer.Companion.getEmpty()) {
                    Function1 function143 = function142;
                    Function1 function144 = function139;
                    Function1 function145 = function135;
                    String string37 = string33;
                    object9 = object80;
                    object11 = list;
                    object13 = boxScope7;
                    GroupActivity groupActivity12 = groupActivity;
                    boolean bl277 = false;
                    string2 = () -> GroupActivity.GroupContent$lambda$259$lambda$258$lambda$257$lambda$233$lambda$232($albumContext, $albumStore, $selectedExistingRecordIds$delegate, $openedAlbumId$delegate, $albumList$delegate);
                    groupActivity = groupActivity12;
                    boxScope7 = object13;
                    list = object11;
                    object80 = object9;
                    string33 = string37;
                    function135 = function145;
                    function139 = function144;
                    function142 = function143;
                    String string38 = string2;
                    $this$cache\210.updateRememberedValue((Object)string38);
                    object76 = string38;
                } else {
                    object76 = object84;
                }
                function1 = (Function0)object76;
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\8);
                groupActivity.GroupAddExistingMemorySheet(boxScope7, list, (Set<String>)object80, string33, (Function1<? super String, Unit>)function135, (Function1<? super String, Unit>)function139, (Function0<Unit>)function142, (Function0<Boolean>)function1, (Composer)$composer\8, 0x1B6000 | 0xE & $changed\8);
                $composer\8.endReplaceGroup();
            } else {
                $composer\8.startReplaceGroup(-1800105230);
                $composer\8.endReplaceGroup();
            }
            if (GroupActivity.GroupContent$lambda$76((MutableState<Boolean>)$showEditAlbumDialog$delegate) && $openedAlbum != null) {
                Object object85;
                void $this$cache\225;
                Object object86;
                void $this$cache\222;
                Object object87;
                void $this$cache\219;
                Object object88;
                void $this$cache\216;
                Object object89;
                void $this$cache\213;
                $composer\8.startReplaceGroup(-1747340936);
                ComposerKt.sourceInformation((Composer)$composer\8, (String)"1045@53513L3,1046@53561L23,1047@53636L30,1048@53708L145,1052@53895L1224,1039@53151L1994");
                GroupActivity groupActivity = this$0;
                BoxScope boxScope8 = boxScope;
                String string39 = "\u7f16\u8f91\u5206\u7ec4";
                String string40 = GroupActivity.GroupContent$lambda$88((MutableState<String>)$albumNameInput$delegate);
                String string41 = GroupActivity.GroupContent$lambda$91((MutableState<String>)$albumDescriptionInput$delegate);
                boolean bl278 = false;
                boolean bl279 = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\8, (int)-1441828781, (String)"CC(remember):GroupActivity.kt#9igjgp");
                void $this$cache\210 = $composer\8;
                boolean bl280 = false;
                boolean bl281 = false;
                Object object90 = $this$cache\213.rememberedValue();
                boolean bl282 = false;
                if (object90 == Composer.Companion.getEmpty()) {
                    boolean bl283 = bl279;
                    boolean bl284 = bl278;
                    String string42 = string41;
                    object9 = string40;
                    object11 = string39;
                    object13 = boxScope8;
                    GroupActivity groupActivity13 = groupActivity;
                    boolean bl285 = false;
                    Function1 function146 = GroupActivity::GroupContent$lambda$259$lambda$258$lambda$257$lambda$235$lambda$234;
                    groupActivity = groupActivity13;
                    boxScope8 = object13;
                    string39 = object11;
                    string40 = object9;
                    string41 = string42;
                    bl278 = bl284;
                    bl279 = bl283;
                    Function1 function147 = function146;
                    $this$cache\213.updateRememberedValue((Object)function147);
                    object89 = function147;
                } else {
                    object89 = object90;
                }
                function1 = (Function1)object89;
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\8);
                Function1 function148 = function1;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\8, (int)-1441827225, (String)"CC(remember):GroupActivity.kt#9igjgp");
                $this$cache\213 = $composer\8;
                boolean bl286 = false;
                boolean bl287 = false;
                Object object91 = $this$cache\216.rememberedValue();
                boolean bl288 = false;
                if (object91 == Composer.Companion.getEmpty()) {
                    Function1 function149 = function148;
                    boolean bl289 = bl279;
                    boolean bl290 = bl278;
                    String string43 = string41;
                    object9 = string40;
                    object11 = string39;
                    object13 = boxScope8;
                    GroupActivity groupActivity14 = groupActivity;
                    boolean bl291 = false;
                    string2 = arg_0 -> GroupActivity.GroupContent$lambda$259$lambda$258$lambda$257$lambda$237$lambda$236($albumNameInput$delegate, arg_0);
                    groupActivity = groupActivity14;
                    boxScope8 = object13;
                    string39 = object11;
                    string40 = object9;
                    string41 = string43;
                    bl278 = bl290;
                    bl279 = bl289;
                    function148 = function149;
                    String string44 = string2;
                    $this$cache\216.updateRememberedValue((Object)string44);
                    object88 = string44;
                } else {
                    object88 = object91;
                }
                function1 = (Function1)object88;
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\8);
                Object object92 = function1;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\8, (int)-1441824818, (String)"CC(remember):GroupActivity.kt#9igjgp");
                $this$cache\216 = $composer\8;
                boolean bl292 = false;
                boolean bl293 = false;
                Object object93 = $this$cache\219.rememberedValue();
                boolean bl294 = false;
                if (object93 == Composer.Companion.getEmpty()) {
                    string2 = object92;
                    Function1 function150 = function148;
                    boolean bl295 = bl279;
                    boolean bl296 = bl278;
                    String string45 = string41;
                    object9 = string40;
                    object11 = string39;
                    object13 = boxScope8;
                    GroupActivity groupActivity15 = groupActivity;
                    boolean bl297 = false;
                    function05 = arg_0 -> GroupActivity.GroupContent$lambda$259$lambda$258$lambda$257$lambda$239$lambda$238($albumDescriptionInput$delegate, arg_0);
                    groupActivity = groupActivity15;
                    boxScope8 = object13;
                    string39 = object11;
                    string40 = object9;
                    string41 = string45;
                    bl278 = bl296;
                    bl279 = bl295;
                    function148 = function150;
                    object92 = string2;
                    Function0 function060 = function05;
                    $this$cache\219.updateRememberedValue((Object)function060);
                    object87 = function060;
                } else {
                    object87 = object93;
                }
                function1 = (Function1)object87;
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\8);
                Function1 function151 = function1;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\8, (int)-1441822399, (String)"CC(remember):GroupActivity.kt#9igjgp");
                $this$cache\219 = $composer\8;
                boolean bl298 = false;
                boolean bl299 = false;
                Object object94 = $this$cache\222.rememberedValue();
                boolean bl300 = false;
                if (object94 == Composer.Companion.getEmpty()) {
                    function05 = function151;
                    string2 = object92;
                    Function1 function152 = function148;
                    boolean bl301 = bl279;
                    boolean bl302 = bl278;
                    String string46 = string41;
                    object9 = string40;
                    object11 = string39;
                    object13 = boxScope8;
                    GroupActivity groupActivity16 = groupActivity;
                    boolean bl303 = false;
                    function02 = () -> GroupActivity.GroupContent$lambda$259$lambda$258$lambda$257$lambda$241$lambda$240($showEditAlbumDialog$delegate, $editingAlbumId$delegate);
                    groupActivity = groupActivity16;
                    boxScope8 = object13;
                    string39 = object11;
                    string40 = object9;
                    string41 = string46;
                    bl278 = bl302;
                    bl279 = bl301;
                    function148 = function152;
                    object92 = string2;
                    function151 = function05;
                    Function0 function061 = function02;
                    $this$cache\222.updateRememberedValue((Object)function061);
                    object86 = function061;
                } else {
                    object86 = object94;
                }
                function1 = (Function0)object86;
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\8);
                Function1 function153 = function1;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\8, (int)-1441815336, (String)"CC(remember):GroupActivity.kt#9igjgp");
                $this$cache\222 = $composer\8;
                boolean bl304 = $composer\8.changedInstance((Object)$openedAlbum) | $composer\8.changedInstance((Object)$albumContext) | $composer\8.changedInstance((Object)$albumStore);
                boolean bl305 = false;
                Object object95 = $this$cache\225.rememberedValue();
                boolean bl306 = false;
                if (bl304 || object95 == Composer.Companion.getEmpty()) {
                    function02 = function153;
                    function05 = function151;
                    string2 = object92;
                    Function1 function154 = function148;
                    boolean bl307 = bl279;
                    boolean bl308 = bl278;
                    String string47 = string41;
                    object9 = string40;
                    object11 = string39;
                    object13 = boxScope8;
                    GroupActivity groupActivity17 = groupActivity;
                    boolean bl309 = false;
                    function0 = () -> GroupActivity.GroupContent$lambda$259$lambda$258$lambda$257$lambda$244$lambda$243($openedAlbum, $albumContext, $albumStore, $editingAlbumId$delegate, $albumNameInput$delegate, $albumList$delegate, $albumDescriptionInput$delegate);
                    groupActivity = groupActivity17;
                    boxScope8 = object13;
                    string39 = object11;
                    string40 = object9;
                    string41 = string47;
                    bl278 = bl308;
                    bl279 = bl307;
                    function148 = function154;
                    object92 = string2;
                    function151 = function05;
                    function153 = function02;
                    Function0 function062 = function0;
                    $this$cache\225.updateRememberedValue((Object)function062);
                    object85 = function062;
                } else {
                    object85 = object95;
                }
                function1 = (Function0)object85;
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\8);
                groupActivity.GroupEditAlbumSheet(boxScope8, string39, string40, string41, bl278, bl279, (Function1<? super Boolean, Unit>)function148, (Function1<? super String, Unit>)object92, (Function1<? super String, Unit>)function151, (Function0<Unit>)function153, (Function0<Boolean>)function1, (Composer)$composer\8, 0x36DB6030 | 0xE & $changed\8, 0);
                $composer\8.endReplaceGroup();
            } else {
                $composer\8.startReplaceGroup(-1800105230);
                $composer\8.endReplaceGroup();
            }
            if (GroupActivity.GroupContent$lambda$79((MutableState<Boolean>)$showDeleteAlbumConfirm$delegate) && $openedAlbum != null) {
                Object object96;
                void function065;
                Object object97;
                void $this$cache\228;
                $composer\8.startReplaceGroup(-1745261952);
                ComposerKt.sourceInformation((Composer)$composer\8, (String)"1077@55447L1095,1096@56584L34,1074@55266L1378");
                String string48 = "\u5220\u9664\u5206\u7ec4";
                String string49 = "\u5220\u9664\u540e\u5c06\u79fb\u9664\u8fd9\u4e2a\u5206\u7ec4\uff0c\u4f46\u4e0d\u4f1a\u5220\u9664\u5176\u4e2d\u7684\u8bb0\u5fc6\u3002\u786e\u5b9a\u7ee7\u7eed\u5417\uff1f";
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\8, (int)-1441765801, (String)"CC(remember):GroupActivity.kt#9igjgp");
                void $this$cache\225 = $composer\8;
                boolean bl310 = $composer\8.changedInstance((Object)$openedAlbum) | $composer\8.changedInstance((Object)$albumStore) | $composer\8.changed($openedAsStandaloneDetail) | $composer\8.changedInstance((Object)$albumContext) | $composer\8.changed((Object)$onCloseAlbumDetail);
                boolean bl311 = false;
                Object object98 = $this$cache\228.rememberedValue();
                boolean bl312 = false;
                if (bl310 || object98 == Composer.Companion.getEmpty()) {
                    object13 = string49;
                    String string50 = string48;
                    boolean bl313 = false;
                    object11 = () -> GroupActivity.GroupContent$lambda$259$lambda$258$lambda$257$lambda$246$lambda$245($openedAlbum, $albumStore, $openedAsStandaloneDetail, $albumContext, $onCloseAlbumDetail, $showDeleteAlbumConfirm$delegate, $closingStandaloneDetail$delegate, $openedAlbumId$delegate, $albumList$delegate);
                    string48 = string50;
                    string49 = object13;
                    Object object99 = object11;
                    $this$cache\228.updateRememberedValue(object99);
                    object97 = object99;
                } else {
                    object97 = object98;
                }
                function1 = (Function0)object97;
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\8);
                Function1 function155 = function1;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\8, (int)-1441730478, (String)"CC(remember):GroupActivity.kt#9igjgp");
                $this$cache\228 = $composer\8;
                boolean bl314 = false;
                boolean bl315 = false;
                Object object100 = function065.rememberedValue();
                boolean bl316 = false;
                if (object100 == Composer.Companion.getEmpty()) {
                    object11 = function155;
                    object13 = string49;
                    String string51 = string48;
                    boolean bl317 = false;
                    object9 = () -> GroupActivity.GroupContent$lambda$259$lambda$258$lambda$257$lambda$248$lambda$247($showDeleteAlbumConfirm$delegate);
                    string48 = string51;
                    string49 = object13;
                    function155 = object11;
                    Object object101 = object9;
                    function065.updateRememberedValue(object101);
                    object96 = object101;
                } else {
                    object96 = object100;
                }
                function1 = (Function0)object96;
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\8);
                ComposeUiKt.NoMemoDeleteConfirmDialog(string48, string49, (Function0<Unit>)function155, (Function0<Unit>)function1, (Composer)$composer\8, 3126);
                $composer\8.endReplaceGroup();
            } else {
                $composer\8.startReplaceGroup(-1800105230);
                $composer\8.endReplaceGroup();
            }
            if (GroupActivity.GroupContent$lambda$70((MutableState<Boolean>)$showRemoveFromAlbumConfirm$delegate) && $openedAlbum != null && !((Collection)$selectedAlbumRecords).isEmpty()) {
                Object object102;
                void $this$cache\237;
                Object object103;
                void $this$cache\234;
                $composer\8.startReplaceGroup(-1743724786);
                ComposerKt.sourceInformation((Composer)$composer\8, (String)"1112@57440L944,1128@58426L38,1102@56873L1617");
                boolean bl318 = $allOpenedRecordsSelected;
                String string52 = bl318 ? "\u5168\u90e8\u79fb\u51fa" : "\u79fb\u51fa\u8bb0\u5fc6";
                Object object104 = bl318 ? "\u786e\u5b9a\u5c06\u8be5\u5206\u7ec4\u4e2d\u7684\u5168\u90e8\u8bb0\u5fc6\u79fb\u51fa\u5417\uff1f" : "\u786e\u5b9a\u5c06\u9009\u4e2d\u7684 " + GroupActivity.GroupContent$lambda$64((MutableState<Set<String>>)$selectedAlbumRecordIds$delegate).size() + " \u6761\u8bb0\u5fc6\u79fb\u51fa\u8fd9\u4e2a\u5206\u7ec4\u5417\uff1f";
                String string53 = bl318 ? "\u5168\u90e8\u79fb\u51fa" : "\u79fb\u51fa";
                String string54 = "\u53d6\u6d88";
                boolean bl319 = true;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\8, (int)-1441702176, (String)"CC(remember):GroupActivity.kt#9igjgp");
                void bl314 = $composer\8;
                boolean bl320 = $composer\8.changedInstance((Object)$albumStore) | $composer\8.changedInstance((Object)$openedAlbum) | $composer\8.changedInstance((Object)$albumContext) | $composer\8.changed(bl318);
                boolean bl321 = false;
                Object object105 = $this$cache\234.rememberedValue();
                boolean bl322 = false;
                if (bl320 || object105 == Composer.Companion.getEmpty()) {
                    boolean bl323 = bl319;
                    object9 = string54;
                    object11 = string53;
                    object13 = object104;
                    String string55 = string52;
                    boolean bl324 = false;
                    Function0 function063 = () -> GroupActivity.GroupContent$lambda$259$lambda$258$lambda$257$lambda$250$lambda$249($albumStore, $openedAlbum, $albumContext, bl318, $selectedAlbumRecordIds$delegate, $showRemoveFromAlbumConfirm$delegate, $albumList$delegate, $albumSelectionModeActive$delegate);
                    string52 = string55;
                    object104 = object13;
                    string53 = object11;
                    string54 = object9;
                    bl319 = bl323;
                    Function0 function064 = function063;
                    $this$cache\234.updateRememberedValue((Object)function064);
                    object103 = function064;
                } else {
                    object103 = object105;
                }
                Function0 function065 = (Function0)object103;
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\8);
                Function0 function066 = function065;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\8, (int)-1441671530, (String)"CC(remember):GroupActivity.kt#9igjgp");
                $this$cache\234 = $composer\8;
                boolean bl325 = false;
                boolean bl326 = false;
                Object object106 = $this$cache\237.rememberedValue();
                boolean bl327 = false;
                if (object106 == Composer.Companion.getEmpty()) {
                    Function0 function067 = function066;
                    boolean bl328 = bl319;
                    object9 = string54;
                    object11 = string53;
                    object13 = object104;
                    String string56 = string52;
                    boolean bl329 = false;
                    Function0 function068 = () -> GroupActivity.GroupContent$lambda$259$lambda$258$lambda$257$lambda$252$lambda$251($showRemoveFromAlbumConfirm$delegate);
                    string52 = string56;
                    object104 = object13;
                    string53 = object11;
                    string54 = object9;
                    bl319 = bl328;
                    function066 = function067;
                    Function0 function069 = function068;
                    $this$cache\237.updateRememberedValue((Object)function069);
                    object102 = function069;
                } else {
                    object102 = object106;
                }
                function065 = (Function0)object102;
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\8);
                ComposeUiKt.NoMemoConfirmDialog(string52, (String)object104, string53, string54, bl319, (Function0<Unit>)function066, (Function0<Unit>)function065, (Composer)$composer\8, 1600512, 0);
                $composer\8.endReplaceGroup();
            } else {
                $composer\8.startReplaceGroup(-1800105230);
                $composer\8.endReplaceGroup();
            }
            if (GroupActivity.GroupContent$lambda$73((MutableState<Boolean>)$showDeleteSelectedConfirm$delegate) && !((Collection)$selectedAlbumRecords).isEmpty()) {
                Object object107;
                void $this$cache\243;
                Object object108;
                void $this$cache\240;
                $composer\8.startReplaceGroup(-1741948610);
                ComposerKt.sourceInformation((Composer)$composer\8, (String)"1134@58691L46,1136@58897L302,1142@59241L37,1133@58628L676");
                String string57 = StringResources_androidKt.stringResource((int)R.string.delete_selected_title, (Composer)$composer\8, (int)0);
                Object[] function065 = new Object[]{GroupActivity.GroupContent$lambda$64((MutableState<Set<String>>)$selectedAlbumRecordIds$delegate).size()};
                String string58 = this$0.getString(R.string.delete_selected_batch_message, function065);
                Intrinsics.checkNotNullExpressionValue((Object)string58, (String)"getString(...)");
                String string59 = string58;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\8, (int)-1441656194, (String)"CC(remember):GroupActivity.kt#9igjgp");
                function065 = $composer\8;
                boolean bl330 = $composer\8.changed((Object)$onDeleteRecords);
                boolean bl331 = false;
                Object object109 = $this$cache\240.rememberedValue();
                boolean bl332 = false;
                if (bl330 || object109 == Composer.Companion.getEmpty()) {
                    object13 = string59;
                    String string60 = string57;
                    boolean bl333 = false;
                    object11 = () -> GroupActivity.GroupContent$lambda$259$lambda$258$lambda$257$lambda$254$lambda$253($onDeleteRecords, $selectedAlbumRecordIds$delegate, $showDeleteSelectedConfirm$delegate, $albumSelectionModeActive$delegate);
                    string57 = string60;
                    string59 = object13;
                    Object object110 = object11;
                    $this$cache\240.updateRememberedValue(object110);
                    object108 = object110;
                } else {
                    object108 = object109;
                }
                string58 = (Function0)object108;
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\8);
                String string61 = string58;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\8, (int)-1441645451, (String)"CC(remember):GroupActivity.kt#9igjgp");
                $this$cache\240 = $composer\8;
                boolean bl334 = false;
                boolean bl335 = false;
                Object object111 = $this$cache\243.rememberedValue();
                boolean bl336 = false;
                if (object111 == Composer.Companion.getEmpty()) {
                    object11 = string61;
                    object13 = string59;
                    String string62 = string57;
                    boolean bl337 = false;
                    object9 = () -> GroupActivity.GroupContent$lambda$259$lambda$258$lambda$257$lambda$256$lambda$255($showDeleteSelectedConfirm$delegate);
                    string57 = string62;
                    string59 = object13;
                    string61 = object11;
                    Object object112 = object9;
                    $this$cache\243.updateRememberedValue(object112);
                    object107 = object112;
                } else {
                    object107 = object111;
                }
                string58 = (Function0)object107;
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\8);
                ComposeUiKt.NoMemoDeleteConfirmDialog(string57, string59, (Function0<Unit>)string61, (Function0<Unit>)string58, (Composer)$composer\8, 3072);
                $composer\8.endReplaceGroup();
            } else {
                $composer\8.startReplaceGroup(-1800105230);
                $composer\8.endReplaceGroup();
            }
            if ($showAddSheet) {
                $composer\8.startReplaceGroup(-1741208609);
                ComposerKt.sourceInformation((Composer)$composer\8, (String)"1147@59392L99");
                AddMemorySheetKt.AddMemorySheet((Function0<Unit>)$onDismissAddSheet, null, (Composer)$composer\8, 0, 2);
                $composer\8.endReplaceGroup();
            } else {
                $composer\8.startReplaceGroup(-1800105230);
                $composer\8.endReplaceGroup();
            }
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\8);
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\7);
            $composer\1.endNode();
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\1);
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\1);
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\1);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        } else {
            $composer.skipToGroupEnd();
        }
        return Unit.INSTANCE;
    }

    @Composable
    @ComposableTarget(applier="androidx.compose.ui.UiComposable")
    private static final Unit GroupContent$lambda$259(NoMemoAdaptiveSpec $albumAdaptive, GroupAlbumStore.GroupAlbum $openedAlbum, Function0 $onOpenMemory, Function0 $onOpenReminder, Function0 $onAddClick, NoMemoDockTab $startupDockPulseTab, List $selectedAlbumRecords, boolean $allOpenedRecordsSelected, GroupActivity this$0, Context $albumContext, Function0 $onOpenSettings, List $openedRecords, SettingsStore $settingsStore, GroupAlbumStore $albumStore, List $filteredExistingRecords, boolean $openedAsStandaloneDetail, Function0 $onCloseAlbumDetail, Function1 $onDeleteRecords, boolean $showAddSheet, Function0 $onDismissAddSheet, float $groupExpandedTitleAlpha, float $groupExpandedTitleTranslateY, LazyListState $groupListState, float $groupListSpacing, List $albumRows, Map $albumPreviewRecordsMap, Function1 $onOpenAlbumDetail, int $albumColumns, LazyListState $albumDetailListState, NoMemoPalette $albumPalette, Function1 $onOpenDetail, MutableState $closingStandaloneDetail$delegate, Function0 $onOpenSearch, float $groupCollapsedTitleAlpha, MutableState $groupListMoreExpanded$delegate, MutableState $groupListMoreAnchorBounds$delegate, State $groupExpandedTitleHeight$delegate, MutableState $albumList$delegate, State $groupListTopPadding$delegate, MutableState $albumSelectionModeActive$delegate, MutableState $selectedAlbumRecordIds$delegate, MutableState $showRemoveFromAlbumConfirm$delegate, MutableState $showDeleteSelectedConfirm$delegate, MutableState $openedAlbumId$delegate, MutableState $detailMoreExpanded$delegate, MutableState $detailMoreAnchorBounds$delegate, MutableState $selectedExistingRecordIds$delegate, MutableState $addExistingSearchQuery$delegate, MutableState $showAddExistingSheet$delegate, MutableState $showCreateAlbumDialog$delegate, MutableState $editingAlbumId$delegate, MutableState $albumNameInput$delegate, MutableState $albumDescriptionInput$delegate, MutableState $showEditAlbumDialog$delegate, MutableState $showDeleteAlbumConfirm$delegate, MutableState $albumAutoClassifyEnabledInput$delegate, BoxScope $this$NoMemoBackground, NoMemoPalette it, Composer $composer, int $changed) {
        Intrinsics.checkNotNullParameter((Object)$this$NoMemoBackground, (String)"$this$NoMemoBackground");
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        ComposerKt.sourceInformation((Composer)$composer, (String)"CN(it)516@22273L37272,516@22228L37317:GroupActivity.kt#83vr7l");
        if ($composer.shouldExecute(($changed & 0x81) != 128, $changed & 1)) {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart((int)862704436, (int)$changed, (int)-1, (String)"com.han.nomemo.GroupActivity.GroupContent.<anonymous> (GroupActivity.kt:516)");
            }
            ComposeUiKt.ResponsiveContentFrame($albumAdaptive, null, (Function4<? super BoxScope, ? super NoMemoAdaptiveSpec, ? super Composer, ? super Integer, Unit>)((Function4)ComposableLambdaKt.rememberComposableLambda((int)-2071553610, (boolean)true, (arg_0, arg_1, arg_2, arg_3) -> GroupActivity.GroupContent$lambda$259$lambda$258($openedAlbum, $onOpenMemory, $onOpenReminder, $onAddClick, $startupDockPulseTab, $selectedAlbumRecords, $allOpenedRecordsSelected, this$0, $albumContext, $onOpenSettings, $openedRecords, $settingsStore, $albumStore, $filteredExistingRecords, $openedAsStandaloneDetail, $onCloseAlbumDetail, $onDeleteRecords, $showAddSheet, $onDismissAddSheet, $groupExpandedTitleAlpha, $groupExpandedTitleTranslateY, $groupListState, $groupListSpacing, $albumRows, $albumPreviewRecordsMap, $onOpenAlbumDetail, $albumColumns, $albumDetailListState, $albumPalette, $onOpenDetail, $albumAdaptive, $closingStandaloneDetail$delegate, $onOpenSearch, $groupCollapsedTitleAlpha, $groupListMoreExpanded$delegate, $groupListMoreAnchorBounds$delegate, $groupExpandedTitleHeight$delegate, $albumList$delegate, $groupListTopPadding$delegate, $albumSelectionModeActive$delegate, $selectedAlbumRecordIds$delegate, $showRemoveFromAlbumConfirm$delegate, $showDeleteSelectedConfirm$delegate, $openedAlbumId$delegate, $detailMoreExpanded$delegate, $detailMoreAnchorBounds$delegate, $selectedExistingRecordIds$delegate, $addExistingSearchQuery$delegate, $showAddExistingSheet$delegate, $showCreateAlbumDialog$delegate, $editingAlbumId$delegate, $albumNameInput$delegate, $albumDescriptionInput$delegate, $showEditAlbumDialog$delegate, $showDeleteAlbumConfirm$delegate, $albumAutoClassifyEnabledInput$delegate, arg_0, arg_1, arg_2, arg_3), (Composer)$composer, (int)54)), $composer, 384, 2);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        } else {
            $composer.skipToGroupEnd();
        }
        return Unit.INSTANCE;
    }

    private static final Unit GroupContent$lambda$260(GroupActivity $tmp55_rcvr, List $allRecords, boolean $hasLoadedRecords, String $selectedCategoryCode, Function1 $onSelectCategory, Function1 $onDeleteRecord, Function1 $onDeleteRecords, Function1 $onOpenDetail, Function0 $onOpenMemory, Function0 $onOpenReminder, Function0 $onOpenSearch, Function0 $onOpenSettings, boolean $showAddSheet, Function0 $onAddClick, Function0 $onDismissAddSheet, NoMemoDockTab $startupDockPulseTab, int $albumRefreshTick, String $initialOpenedAlbumId, boolean $openedAsStandaloneDetail, Function1 $onOpenAlbumDetail, Function0 $onCloseAlbumDetail, int $$changed, int $$changed1, int $$changed2, Composer $composer, int $force) {
        $tmp55_rcvr.GroupContent($allRecords, $hasLoadedRecords, $selectedCategoryCode, (Function1<? super String, Unit>)$onSelectCategory, (Function1<? super MemoryRecord, Unit>)$onDeleteRecord, (Function1<? super Set<String>, Unit>)$onDeleteRecords, (Function1<? super MemoryRecord, Unit>)$onOpenDetail, (Function0<Unit>)$onOpenMemory, (Function0<Unit>)$onOpenReminder, (Function0<Unit>)$onOpenSearch, (Function0<Unit>)$onOpenSettings, $showAddSheet, (Function0<Unit>)$onAddClick, (Function0<Unit>)$onDismissAddSheet, $startupDockPulseTab, $albumRefreshTick, $initialOpenedAlbumId, $openedAsStandaloneDetail, (Function1<? super String, Unit>)$onOpenAlbumDetail, (Function0<Unit>)$onCloseAlbumDetail, $composer, RecomposeScopeImplKt.updateChangedFlags((int)($$changed | 1)), RecomposeScopeImplKt.updateChangedFlags((int)$$changed1), RecomposeScopeImplKt.updateChangedFlags((int)$$changed2));
        return Unit.INSTANCE;
    }

    @Composable
    @ComposableTarget(applier="androidx.compose.ui.UiComposable")
    private static final Unit GroupAlbumGridCard$lambda$263$lambda$262(GroupActivity this$0, GroupAlbumStore.GroupAlbum $album, List $previewRecords, int $memoryCount, String $dayText, boolean $compact, ColumnScope $this$Card, Composer $composer, int $changed) {
        Intrinsics.checkNotNullParameter((Object)$this$Card, (String)"$this$Card");
        ComposerKt.sourceInformation((Composer)$composer, (String)"C1186@60716L363:GroupActivity.kt#83vr7l");
        if ($composer.shouldExecute(($changed & 0x11) != 16, $changed & 1)) {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart((int)-148027267, (int)$changed, (int)-1, (String)"com.han.nomemo.GroupActivity.GroupAlbumGridCard.<anonymous>.<anonymous> (GroupActivity.kt:1186)");
            }
            String string2 = $album.getAlbumId();
            String string3 = $album.getName();
            Intrinsics.checkNotNull((Object)$dayText);
            int n = 10;
            boolean bl = false;
            this$0.GroupAlbumCoverCollage(string2, string3, $previewRecords, $memoryCount, $dayText, $compact, PaddingKt.padding-3ABfNKs((Modifier)((Modifier)Modifier.Companion), (float)Dp.constructor-impl((float)n)), $composer, 0x180000, 0);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        } else {
            $composer.skipToGroupEnd();
        }
        return Unit.INSTANCE;
    }

    @Composable
    @ComposableTarget(applier="androidx.compose.ui.UiComposable")
    private static final Unit GroupAlbumGridCard$lambda$263(ContinuousRoundedRectangle $cardShape, boolean $isDark, GroupActivity this$0, GroupAlbumStore.GroupAlbum $album, List $previewRecords, int $memoryCount, String $dayText, boolean $compact, BoxScope $this$PressScaleBox, Composer $composer, int $changed) {
        Intrinsics.checkNotNullParameter((Object)$this$PressScaleBox, (String)"$this$PressScaleBox");
        ComposerKt.sourceInformation((Composer)$composer, (String)"C1176@60319L223,1182@60585L98,1185@60698L395,1174@60240L853:GroupActivity.kt#83vr7l");
        if ($composer.shouldExecute(($changed & 0x11) != 16, $changed & 1)) {
            float f;
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart((int)1200204427, (int)$changed, (int)-1, (String)"com.han.nomemo.GroupActivity.GroupAlbumGridCard.<anonymous> (GroupActivity.kt:1174)");
            }
            Shape shape = (Shape)$cardShape;
            CardColors cardColors = CardDefaults.INSTANCE.cardColors-ro_MJ88(ComposeUiKt.noMemoCardSurfaceColor-4WTKRHQ($isDark, Color.copy-wmQWz5c$default((long)Color.Companion.getWhite-0d7_KjU(), (float)0.995f, (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null)), 0L, 0L, 0L, $composer, CardDefaults.$stable << 12, 14);
            if ($isDark) {
                boolean bl = false;
                boolean bl2 = false;
                f = Dp.constructor-impl((float)((float)bl));
            } else {
                int n = 2;
                boolean bl = false;
                f = Dp.constructor-impl((float)n);
            }
            CardKt.Card(null, (Shape)shape, (CardColors)cardColors, (CardElevation)CardDefaults.INSTANCE.cardElevation-aqJV_2Y(f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, $composer, CardDefaults.$stable << 18, 62), null, (Function3)((Function3)ComposableLambdaKt.rememberComposableLambda((int)-148027267, (boolean)true, (arg_0, arg_1, arg_2) -> GroupActivity.GroupAlbumGridCard$lambda$263$lambda$262(this$0, $album, $previewRecords, $memoryCount, $dayText, $compact, arg_0, arg_1, arg_2), (Composer)$composer, (int)54)), (Composer)$composer, (int)196608, (int)17);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        } else {
            $composer.skipToGroupEnd();
        }
        return Unit.INSTANCE;
    }

    private static final Unit GroupAlbumGridCard$lambda$264(GroupActivity $tmp2_rcvr, GroupAlbumStore.GroupAlbum $album, boolean $compact, int $memoryCount, List $previewRecords, Modifier $modifier, Function0 $onClick, int $$changed, int $$default, Composer $composer, int $force) {
        $tmp2_rcvr.GroupAlbumGridCard($album, $compact, $memoryCount, $previewRecords, $modifier, (Function0<Unit>)$onClick, $composer, RecomposeScopeImplKt.updateChangedFlags((int)($$changed | 1)), $$default);
        return Unit.INSTANCE;
    }

    private static final Unit GroupAlbumCoverCollage$lambda$275$lambda$266$lambda$265(GraphicsLayerScope $this$graphicsLayer) {
        Intrinsics.checkNotNullParameter((Object)$this$graphicsLayer, (String)"$this$graphicsLayer");
        $this$graphicsLayer.setRotationZ(-4.0f);
        return Unit.INSTANCE;
    }

    private static final Unit GroupAlbumCoverCollage$lambda$275$lambda$268$lambda$267(GraphicsLayerScope $this$graphicsLayer) {
        Intrinsics.checkNotNullParameter((Object)$this$graphicsLayer, (String)"$this$graphicsLayer");
        $this$graphicsLayer.setRotationZ(5.0f);
        return Unit.INSTANCE;
    }

    private static final Unit GroupAlbumCoverCollage$lambda$276(GroupActivity $tmp0_rcvr, String $albumId, String $albumName, List $previewRecords, int $memoryCount, String $dayText, boolean $compact, Modifier $modifier, int $$changed, int $$default, Composer $composer, int $force) {
        $tmp0_rcvr.GroupAlbumCoverCollage($albumId, $albumName, $previewRecords, $memoryCount, $dayText, $compact, $modifier, $composer, RecomposeScopeImplKt.updateChangedFlags((int)($$changed | 1)), $$default);
        return Unit.INSTANCE;
    }

    private static final Unit GroupAlbumFoilTitle$lambda$278(GroupActivity $tmp0_rcvr, String $text, boolean $compact, Modifier $modifier, int $$changed, int $$default, Composer $composer, int $force) {
        $tmp0_rcvr.GroupAlbumFoilTitle($text, $compact, $modifier, $composer, RecomposeScopeImplKt.updateChangedFlags((int)($$changed | 1)), $$default);
        return Unit.INSTANCE;
    }

    /*
     * WARNING - void declaration
     */
    private static final Unit GroupAlbumPaperTexture$lambda$281$lambda$280$lambda$279(long $topSheen, long $bottomShade, long $verticalLineColor, float $strokeWidth, float $verticalSpacing, long $diagonalLineColor, float $diagonalSpacing, ContentDrawScope $this$onDrawWithContent) {
        Intrinsics.checkNotNullParameter((Object)$this$onDrawWithContent, (String)"$this$onDrawWithContent");
        $this$onDrawWithContent.drawContent();
        Object[] objectArray = new Color[]{Color.box-impl((long)$topSheen), Color.box-impl((long)Color.Companion.getTransparent-0d7_KjU()), Color.box-impl((long)$bottomShade)};
        DrawScope.drawRect-AsUm42w$default((DrawScope)((DrawScope)$this$onDrawWithContent), (Brush)Brush.Companion.verticalGradient-8A-3gB4$default((Brush.Companion)Brush.Companion, (List)CollectionsKt.listOf((Object[])objectArray), (float)0.0f, (float)0.0f, (int)0, (int)14, null), (long)0L, (long)0L, (float)0.0f, null, null, (int)0, (int)126, null);
        float x = 0.0f;
        while (true) {
            void y\9;
            void x\9;
            float f;
            long arg0\22 = $this$onDrawWithContent.getSize-NH-jbRc();
            boolean bl = false;
            long l = arg0\22;
            boolean bl2 = false;
            int n = (int)(l >> 32);
            boolean bl3 = false;
            if (!(x <= Float.intBitsToFloat(n))) break;
            float arg0\22 = x;
            float f2 = 0.0f;
            boolean bl4 = false;
            boolean bl5 = false;
            long l2 = Float.floatToRawIntBits(f);
            long l3 = Float.floatToRawIntBits(f2);
            f = x;
            long arg0\72 = $this$onDrawWithContent.getSize-NH-jbRc();
            boolean bl6 = false;
            long l4 = arg0\72;
            boolean bl7 = false;
            int n2 = (int)(l4 & 0xFFFFFFFFL);
            boolean bl8 = false;
            float arg0\72 = Float.intBitsToFloat(n2);
            boolean bl9 = false;
            boolean bl10 = false;
            long l5 = Float.floatToRawIntBits((float)x\9);
            long l6 = Float.floatToRawIntBits((float)y\9);
            DrawScope.drawLine-NGM6Ib0$default((DrawScope)((DrawScope)$this$onDrawWithContent), (long)$verticalLineColor, (long)Offset.constructor-impl((long)(l2 << 32 | l3 & 0xFFFFFFFFL)), (long)Offset.constructor-impl((long)(l5 << 32 | l6 & 0xFFFFFFFFL)), (float)$strokeWidth, (int)0, null, (float)0.0f, null, (int)0, (int)496, null);
            x += $verticalSpacing;
        }
        long l = $this$onDrawWithContent.getSize-NH-jbRc();
        boolean bl = false;
        long l7 = l;
        boolean bl11 = false;
        int n = (int)(l7 & 0xFFFFFFFFL);
        boolean bl12 = false;
        float diagonalX = -Float.intBitsToFloat(n);
        while (true) {
            void x\25;
            void y\20;
            void x\20;
            long arg0\152 = $this$onDrawWithContent.getSize-NH-jbRc();
            boolean bl13 = false;
            long l8 = arg0\152;
            boolean bl14 = false;
            int n3 = (int)(l8 >> 32);
            boolean bl15 = false;
            if (!(diagonalX <= Float.intBitsToFloat(n3))) break;
            float arg0\152 = diagonalX;
            long arg0\182 = $this$onDrawWithContent.getSize-NH-jbRc();
            boolean bl16 = false;
            long l9 = arg0\182;
            boolean bl17 = false;
            int n4 = (int)(l9 & 0xFFFFFFFFL);
            boolean bl18 = false;
            float arg0\182 = Float.intBitsToFloat(n4);
            boolean bl19 = false;
            boolean bl20 = false;
            long l10 = Float.floatToRawIntBits((float)x\20);
            long l11 = Float.floatToRawIntBits((float)y\20);
            long arg0\232 = $this$onDrawWithContent.getSize-NH-jbRc();
            boolean bl21 = false;
            long l12 = arg0\232;
            boolean bl22 = false;
            int n5 = (int)(l12 & 0xFFFFFFFFL);
            boolean bl23 = false;
            float arg0\232 = diagonalX + Float.intBitsToFloat(n5) * 0.62f;
            float f = 0.0f;
            boolean bl24 = false;
            boolean bl25 = false;
            long l13 = Float.floatToRawIntBits((float)x\25);
            long l14 = Float.floatToRawIntBits(f);
            DrawScope.drawLine-NGM6Ib0$default((DrawScope)((DrawScope)$this$onDrawWithContent), (long)$diagonalLineColor, (long)Offset.constructor-impl((long)(l10 << 32 | l11 & 0xFFFFFFFFL)), (long)Offset.constructor-impl((long)(l13 << 32 | l14 & 0xFFFFFFFFL)), (float)$strokeWidth, (int)0, null, (float)0.0f, null, (int)0, (int)496, null);
            diagonalX += $diagonalSpacing;
        }
        return Unit.INSTANCE;
    }

    private static final DrawResult GroupAlbumPaperTexture$lambda$281$lambda$280(long $topSheen, long $bottomShade, long $verticalLineColor, long $diagonalLineColor, CacheDrawScope $this$drawWithCache) {
        Intrinsics.checkNotNullParameter((Object)$this$drawWithCache, (String)"$this$drawWithCache");
        int n = 18;
        boolean bl = false;
        float verticalSpacing = $this$drawWithCache.toPx-0680j_4(Dp.constructor-impl((float)n));
        int n2 = 46;
        boolean bl2 = false;
        float diagonalSpacing = $this$drawWithCache.toPx-0680j_4(Dp.constructor-impl((float)n2));
        boolean bl3 = true;
        boolean bl4 = false;
        float strokeWidth = $this$drawWithCache.toPx-0680j_4(Dp.constructor-impl((float)((float)bl3)));
        return $this$drawWithCache.onDrawWithContent(arg_0 -> GroupActivity.GroupAlbumPaperTexture$lambda$281$lambda$280$lambda$279($topSheen, $bottomShade, $verticalLineColor, strokeWidth, verticalSpacing, $diagonalLineColor, diagonalSpacing, arg_0));
    }

    private static final Unit GroupAlbumPaperTexture$lambda$282(GroupActivity $tmp1_rcvr, boolean $isDark, Modifier $modifier, int $$changed, int $$default, Composer $composer, int $force) {
        $tmp1_rcvr.GroupAlbumPaperTexture($isDark, $modifier, $composer, RecomposeScopeImplKt.updateChangedFlags((int)($$changed | 1)), $$default);
        return Unit.INSTANCE;
    }

    private static final Unit GroupAlbumCoverTile_DzVHIIc$lambda$284$lambda$283(float $rotationZ, GraphicsLayerScope $this$graphicsLayer) {
        Intrinsics.checkNotNullParameter((Object)$this$graphicsLayer, (String)"$this$graphicsLayer");
        $this$graphicsLayer.setRotationZ($rotationZ);
        return Unit.INSTANCE;
    }

    /*
     * WARNING - void declaration
     */
    @Composable
    @ComposableTarget(applier="androidx.compose.ui.UiComposable")
    private static final Unit GroupAlbumCoverTile_DzVHIIc$lambda$286(MemoryRecord $record, float $innerCorner, long $fallbackBackground, GroupActivity this$0, boolean $isDark, BoxWithConstraintsScope $this$BoxWithConstraints, Composer $composer, int $changed) {
        Intrinsics.checkNotNullParameter((Object)$this$BoxWithConstraints, (String)"$this$BoxWithConstraints");
        ComposerKt.sourceInformation((Composer)$composer, (String)"C1586@75571L1974:GroupActivity.kt#83vr7l");
        int $dirty = $changed;
        if (($changed & 6) == 0) {
            $dirty |= $composer.changed((Object)$this$BoxWithConstraints) ? 4 : 2;
        }
        if ($composer.shouldExecute(($dirty & 0x13) != 18, $dirty & 1)) {
            Object[] objectArray;
            void $composer\12;
            void $changed\7;
            void $changed\6;
            void modifier\6;
            void $changed\5;
            void modifier\5;
            void $composer\5;
            void other\4;
            void arg0\52;
            float f;
            void arg0\2;
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart((int)-1845516899, (int)$dirty, (int)-1, (String)"com.han.nomemo.GroupActivity.GroupAlbumCoverTile.<anonymous> (GroupActivity.kt:1583)");
            }
            MemoryRecord currentRecord = $record;
            float f2 = $this$BoxWithConstraints.getMaxWidth-D9Ej5fM();
            int $this$dp\22 = 8;
            boolean bl = false;
            float $this$dp\22 = Dp.constructor-impl((float)$this$dp\22);
            boolean bl2 = false;
            float contentWidth = Dp.constructor-impl((float)(arg0\2 - f));
            f = $this$BoxWithConstraints.getMaxHeight-D9Ej5fM();
            int $this$dp\42 = 8;
            boolean bl3 = false;
            float $this$dp\42 = Dp.constructor-impl((float)$this$dp\42);
            boolean bl4 = false;
            float contentHeight = Dp.constructor-impl((float)(arg0\52 - other\4));
            Modifier arg0\52 = ClipKt.clip((Modifier)SizeKt.fillMaxSize$default((Modifier)((Modifier)Modifier.Companion), (float)0.0f, (int)1, null), (Shape)((Shape)ComposeUiKt.noMemoG2RoundedShape-0680j_4($innerCorner)));
            Composer composer = $composer;
            boolean bl5 = false;
            boolean bl6 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\5, (int)1042775818, (String)"CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo");
            Alignment alignment = Alignment.Companion.getTopStart();
            boolean bl7 = false;
            MeasurePolicy measurePolicy = BoxKt.maybeCachedBoxMeasurePolicy((Alignment)alignment, (boolean)bl7);
            void var20_23 = modifier\5;
            int n = 0x70 & $changed\5 << 3;
            boolean bl8 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\5, (int)-1159599143, (String)"CC(Layout)P(!1,2)80@3267L27,83@3433L360:Layout.kt#80mrfh");
            int n2 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode((Composer)$composer\5, (int)0));
            CompositionLocalMap compositionLocalMap = $composer\5.getCurrentCompositionLocalMap();
            Modifier modifier = ComposedModifierKt.materializeModifier((Composer)$composer\5, (Modifier)modifier\6);
            Function0 function0 = ComposeUiNode.Companion.getConstructor();
            int n3 = 6 | 0x380 & $changed\6 << 6;
            boolean bl9 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\5, (int)-553112988, (String)"CC(ReusableComposeNode)N(factory,update,content)399@15590L9:Composables.kt#9igjgp");
            if (!($composer\5.getApplier() instanceof Applier)) {
                ComposablesKt.invalidApplier();
            }
            $composer\5.startReusableNode();
            if ($composer\5.getInserting()) {
                void factory\7;
                $composer\5.createNode((Function0)factory\7);
            } else {
                $composer\5.useNode();
            }
            Composer composer2 = Updater.constructor-impl((Composer)$composer\5);
            boolean bl10 = false;
            Updater.set-impl((Composer)composer2, (Object)measurePolicy, (Function2)ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.set-impl((Composer)composer2, (Object)compositionLocalMap, (Function2)ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            Function2 function2 = ComposeUiNode.Companion.getSetCompositeKeyHash();
            boolean bl11 = false;
            Composer composer3 = composer2;
            boolean bl12 = false;
            if (composer3.getInserting() || !Intrinsics.areEqual((Object)composer3.rememberedValue(), (Object)n2)) {
                composer3.updateRememberedValue((Object)n2);
                composer2.apply((Object)n2, function2);
            }
            Updater.set-impl((Composer)composer2, (Object)modifier, (Function2)ComposeUiNode.Companion.getSetModifier());
            int n4 = 0xE & $changed\7 >> 6;
            void $composer\11 = $composer\5;
            boolean bl13 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\11, (int)1833054614, (String)"C72@3469L9:Box.kt#2w3rfo");
            int n5 = 6 | 0x70 & $changed\5 >> 6;
            void var39_42 = $composer\11;
            BoxScope boxScope = (BoxScope)BoxScopeInstance.INSTANCE;
            boolean bl14 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\12, (int)1685858925, (String)"C1619@77052L479:GroupActivity.kt#83vr7l");
            Object object = currentRecord;
            boolean bl15 = object != null && (object = object.getImageUri()) != null ? !StringsKt.isBlank((CharSequence)((CharSequence)object)) : false;
            if (bl15) {
                $composer\12.startReplaceGroup(1685870797);
                ComposerKt.sourceInformation((Composer)$composer\12, (String)"1592@75817L374");
                String string2 = currentRecord.getImageUri();
                if (string2 == null) {
                    string2 = "";
                }
                ComposeUiKt.MemoryThumbnail-Jmzb884(string2, contentWidth, contentHeight, SizeKt.fillMaxSize$default((Modifier)((Modifier)Modifier.Companion), (float)0.0f, (int)1, null), $fallbackBackground, $innerCorner, (Composer)$composer\12, 3072, 0);
                $composer\12.endReplaceGroup();
            } else {
                $composer\12.startReplaceGroup(1686299992);
                ComposerKt.sourceInformation((Composer)$composer\12, (String)"1601@76237L219,1606@76477L539");
                MemoryRecord memoryRecord = currentRecord;
                BoxKt.Box((Modifier)BackgroundKt.background$default((Modifier)SizeKt.fillMaxSize$default((Modifier)((Modifier)Modifier.Companion), (float)0.0f, (int)1, null), (Brush)this$0.groupAlbumFallbackTileBrush(memoryRecord != null ? memoryRecord.getCategoryCode() : null, $isDark), null, (float)0.0f, (int)6, null), (Composer)$composer\12, (int)0);
                objectArray = this$0.groupAlbumFallbackTileLabel(currentRecord);
                long l = Color.copy-wmQWz5c$default((long)Color.Companion.getWhite-0d7_KjU(), (float)0.96f, (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null);
                long l2 = TextUnitKt.getSp((int)12);
                FontWeight fontWeight = FontWeight.Companion.getSemiBold();
                int n6 = TextOverflow.Companion.getEllipsis-gIe3tQ8();
                int n7 = 10;
                boolean bl16 = false;
                int n8 = 9;
                boolean bl17 = false;
                Modifier modifier2 = PaddingKt.padding-VpY3zN4((Modifier)boxScope.align((Modifier)Modifier.Companion, Alignment.Companion.getBottomStart()), (float)Dp.constructor-impl((float)n7), (float)Dp.constructor-impl((float)n8));
                TextKt.Text--4IGK_g((String)objectArray, (Modifier)modifier2, (long)l, (long)l2, null, (FontWeight)fontWeight, null, (long)0L, null, null, (long)0L, (int)n6, (boolean)false, (int)2, (int)0, null, null, (Composer)$composer\12, (int)200064, (int)3120, (int)120784);
                $composer\12.endReplaceGroup();
            }
            objectArray = new Color[]{Color.box-impl((long)Color.Companion.getTransparent-0d7_KjU()), Color.box-impl((long)Color.copy-wmQWz5c$default((long)Color.Companion.getBlack-0d7_KjU(), (float)($isDark ? 0.1f : 0.08f), (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null))};
            BoxKt.Box((Modifier)BackgroundKt.background$default((Modifier)boxScope.matchParentSize((Modifier)Modifier.Companion), (Brush)Brush.Companion.verticalGradient-8A-3gB4$default((Brush.Companion)Brush.Companion, (List)CollectionsKt.listOf((Object[])objectArray), (float)0.0f, (float)0.0f, (int)0, (int)14, null), null, (float)0.0f, (int)6, null), (Composer)$composer\12, (int)0);
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\12);
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\11);
            $composer\5.endNode();
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\5);
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\5);
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\5);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        } else {
            $composer.skipToGroupEnd();
        }
        return Unit.INSTANCE;
    }

    private static final Unit GroupAlbumCoverTile_DzVHIIc$lambda$287(GroupActivity $tmp1_rcvr, MemoryRecord $record, float $cornerRadius, float $rotationZ, Modifier $modifier, int $$changed, int $$default, Composer $composer, int $force) {
        $tmp1_rcvr.GroupAlbumCoverTile-DzVHIIc($record, $cornerRadius, $rotationZ, $modifier, $composer, RecomposeScopeImplKt.updateChangedFlags((int)($$changed | 1)), $$default);
        return Unit.INSTANCE;
    }

    /*
     * WARNING - void declaration
     */
    private static final boolean GroupAddExistingMemorySheet$lambda$289(MutableState<Boolean> $visible$delegate) {
        void $this$getValue\1;
        State state = (State)$visible$delegate;
        Object var2_2 = null;
        Object property\1 = null;
        boolean bl = false;
        return (Boolean)$this$getValue\1.getValue();
    }

    /*
     * WARNING - void declaration
     */
    private static final void GroupAddExistingMemorySheet$lambda$290(MutableState<Boolean> $visible$delegate, boolean bl) {
        void $this$setValue\1;
        MutableState<Boolean> mutableState = $visible$delegate;
        Object var3_3 = null;
        Object var4_4 = null;
        Boolean bl2 = bl;
        boolean bl3 = false;
        $this$setValue\1.setValue((Object)bl2);
    }

    /*
     * WARNING - void declaration
     */
    private static final boolean GroupAddExistingMemorySheet$lambda$292(MutableState<Boolean> $dismissCommitted$delegate) {
        void $this$getValue\1;
        State state = (State)$dismissCommitted$delegate;
        Object var2_2 = null;
        Object property\1 = null;
        boolean bl = false;
        return (Boolean)$this$getValue\1.getValue();
    }

    /*
     * WARNING - void declaration
     */
    private static final void GroupAddExistingMemorySheet$lambda$293(MutableState<Boolean> $dismissCommitted$delegate, boolean bl) {
        void $this$setValue\1;
        MutableState<Boolean> mutableState = $dismissCommitted$delegate;
        Object var3_3 = null;
        Object var4_4 = null;
        Boolean bl2 = bl;
        boolean bl3 = false;
        $this$setValue\1.setValue((Object)bl2);
    }

    private static final boolean GroupAddExistingMemorySheet$lambda$297$lambda$296(MutableState $visible$delegate) {
        GroupActivity.GroupAddExistingMemorySheet$lambda$290((MutableState<Boolean>)$visible$delegate, false);
        return true;
    }

    private static final Unit GroupAddExistingMemorySheet$lambda$299$lambda$298(Function0 $onConfirm, MutableState $visible$delegate) {
        if (((Boolean)$onConfirm.invoke()).booleanValue()) {
            GroupActivity.GroupAddExistingMemorySheet$lambda$290((MutableState<Boolean>)$visible$delegate, false);
        }
        return Unit.INSTANCE;
    }

    private static final Unit GroupAddExistingMemorySheet$lambda$301$lambda$300(Function0 $tryDismiss) {
        $tryDismiss.invoke();
        return Unit.INSTANCE;
    }

    private static final Unit GroupAddExistingMemorySheet$lambda$331$lambda$305$lambda$304$lambda$303(Function0 $tryDismiss) {
        $tryDismiss.invoke();
        return Unit.INSTANCE;
    }

    /*
     * WARNING - void declaration
     */
    @Composable
    @ComposableTarget(applier="androidx.compose.ui.UiComposable")
    private static final Unit GroupAddExistingMemorySheet$lambda$331$lambda$305(boolean $isDark, NoMemoSheetDragController $sheetDrag, Function0 $tryDismiss, AnimatedVisibilityScope $this$AnimatedVisibility, Composer $composer, int $changed) {
        Object object;
        void $this$cache\4;
        Object object2;
        Modifier modifier;
        Composer composer;
        Intrinsics.checkNotNullParameter((Object)$this$AnimatedVisibility, (String)"$this$AnimatedVisibility");
        ComposerKt.sourceInformation((Composer)$composer, (String)"C1720@80493L39,1722@80619L16,1711@80084L595:GroupActivity.kt#83vr7l");
        if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart((int)-855191084, (int)$changed, (int)-1, (String)"com.han.nomemo.GroupActivity.GroupAddExistingMemorySheet.<anonymous>.<anonymous> (GroupActivity.kt:1711)");
        }
        Modifier modifier2 = BackgroundKt.background-bw27NRU$default((Modifier)SizeKt.fillMaxSize$default((Modifier)((Modifier)Modifier.Companion), (float)0.0f, (int)1, null), (long)Color.copy-wmQWz5c$default((long)Color.Companion.getBlack-0d7_KjU(), (float)(($isDark ? 0.56f : 0.28f) * $sheetDrag.getScrimAlphaFraction()), (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null), null, (int)2, null);
        ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)-1499864485, (String)"CC(remember):GroupActivity.kt#9igjgp");
        Composer composer2 = $composer;
        boolean bl = false;
        boolean bl2 = false;
        Object object3 = composer.rememberedValue();
        boolean bl3 = false;
        if (object3 == Composer.Companion.getEmpty()) {
            modifier = modifier2;
            boolean bl4 = false;
            modifier2 = modifier;
            MutableInteractionSource mutableInteractionSource = InteractionSourceKt.MutableInteractionSource();
            composer.updateRememberedValue((Object)mutableInteractionSource);
            object2 = mutableInteractionSource;
        } else {
            object2 = object3;
        }
        MutableInteractionSource mutableInteractionSource = (MutableInteractionSource)object2;
        ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
        MutableInteractionSource mutableInteractionSource2 = mutableInteractionSource;
        Indication indication = null;
        boolean bl5 = false;
        String string2 = null;
        Role role = null;
        ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)-1499860476, (String)"CC(remember):GroupActivity.kt#9igjgp");
        composer = $composer;
        boolean bl6 = false;
        boolean bl7 = false;
        Object object4 = $this$cache\4.rememberedValue();
        boolean bl8 = false;
        if (object4 == Composer.Companion.getEmpty()) {
            Role role2 = role;
            String string3 = string2;
            boolean bl9 = bl5;
            Indication indication2 = indication;
            MutableInteractionSource mutableInteractionSource3 = mutableInteractionSource2;
            modifier = modifier2;
            boolean bl10 = false;
            Function0 function0 = () -> GroupActivity.GroupAddExistingMemorySheet$lambda$331$lambda$305$lambda$304$lambda$303($tryDismiss);
            modifier2 = modifier;
            mutableInteractionSource2 = mutableInteractionSource3;
            indication = indication2;
            bl5 = bl9;
            string2 = string3;
            role = role2;
            Function0 function02 = function0;
            $this$cache\4.updateRememberedValue((Object)function02);
            object = function02;
        } else {
            object = object4;
        }
        mutableInteractionSource = (Function0)object;
        ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
        BoxKt.Box((Modifier)ClickableKt.clickable-O2vRcR0$default((Modifier)modifier2, (MutableInteractionSource)mutableInteractionSource2, indication, (boolean)bl5, string2, role, (Function0)mutableInteractionSource, (int)28, null), (Composer)$composer, (int)0);
        if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
        }
        return Unit.INSTANCE;
    }

    private static final int GroupAddExistingMemorySheet$lambda$331$lambda$307$lambda$306(int fullHeight) {
        return fullHeight;
    }

    private static final int GroupAddExistingMemorySheet$lambda$331$lambda$309$lambda$308(int fullHeight) {
        return fullHeight;
    }

    private static final Unit GroupAddExistingMemorySheet$lambda$331$lambda$330$lambda$329$lambda$328$lambda$313$lambda$311$lambda$310(Function0 $tryDismiss) {
        $tryDismiss.invoke();
        return Unit.INSTANCE;
    }

    /*
     * WARNING - void declaration
     */
    @Composable
    @ComposableInferredTarget(scheme="[androidx.compose.ui.UiComposable[androidx.compose.ui.UiComposable]]")
    private static final Unit GroupAddExistingMemorySheet$lambda$331$lambda$330$lambda$329$lambda$328$lambda$320$lambda$319$lambda$315(String $searchQuery, NoMemoPalette $palette, Function2 innerTextField, Composer $composer, int $changed) {
        Intrinsics.checkNotNullParameter((Object)innerTextField, (String)"innerTextField");
        ComposerKt.sourceInformation((Composer)$composer, (String)"CN(innerTextField)1829@85805L698:GroupActivity.kt#83vr7l");
        int $dirty = $changed;
        if (($changed & 6) == 0) {
            $dirty |= $composer.changedInstance((Object)innerTextField) ? 4 : 2;
        }
        if ($composer.shouldExecute(($dirty & 0x13) != 18, $dirty & 1)) {
            void $composer\8;
            void $changed\3;
            void $changed\2;
            void modifier\2;
            void $changed\1;
            void modifier\1;
            void contentAlignment\1;
            void $composer\1;
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart((int)-602975070, (int)$dirty, (int)-1, (String)"com.han.nomemo.GroupActivity.GroupAddExistingMemorySheet.<anonymous>.<anonymous>.<anonymous>.<anonymous>.<anonymous>.<anonymous>.<anonymous> (GroupActivity.kt:1829)");
            }
            Modifier modifier = SizeKt.fillMaxWidth$default((Modifier)((Modifier)Modifier.Companion), (float)0.0f, (int)1, null);
            Alignment alignment = Alignment.Companion.getCenterStart();
            Composer composer = $composer;
            int n = 54;
            boolean bl = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\1, (int)1042775818, (String)"CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo");
            boolean bl2 = false;
            MeasurePolicy measurePolicy = BoxKt.maybeCachedBoxMeasurePolicy((Alignment)contentAlignment\1, (boolean)bl2);
            void var13_13 = modifier\1;
            int n2 = 0x70 & $changed\1 << 3;
            boolean bl3 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\1, (int)-1159599143, (String)"CC(Layout)P(!1,2)80@3267L27,83@3433L360:Layout.kt#80mrfh");
            int n3 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode((Composer)$composer\1, (int)0));
            CompositionLocalMap compositionLocalMap = $composer\1.getCurrentCompositionLocalMap();
            Modifier modifier2 = ComposedModifierKt.materializeModifier((Composer)$composer\1, (Modifier)modifier\2);
            Function0 function0 = ComposeUiNode.Companion.getConstructor();
            int n4 = 6 | 0x380 & $changed\2 << 6;
            boolean bl4 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\1, (int)-553112988, (String)"CC(ReusableComposeNode)N(factory,update,content)399@15590L9:Composables.kt#9igjgp");
            if (!($composer\1.getApplier() instanceof Applier)) {
                ComposablesKt.invalidApplier();
            }
            $composer\1.startReusableNode();
            if ($composer\1.getInserting()) {
                void factory\3;
                $composer\1.createNode((Function0)factory\3);
            } else {
                $composer\1.useNode();
            }
            Composer composer2 = Updater.constructor-impl((Composer)$composer\1);
            boolean bl5 = false;
            Updater.set-impl((Composer)composer2, (Object)measurePolicy, (Function2)ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.set-impl((Composer)composer2, (Object)compositionLocalMap, (Function2)ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            Function2 function2 = ComposeUiNode.Companion.getSetCompositeKeyHash();
            boolean bl6 = false;
            Composer composer3 = composer2;
            boolean bl7 = false;
            if (composer3.getInserting() || !Intrinsics.areEqual((Object)composer3.rememberedValue(), (Object)n3)) {
                composer3.updateRememberedValue((Object)n3);
                composer2.apply((Object)n3, function2);
            }
            Updater.set-impl((Composer)composer2, (Object)modifier2, (Function2)ComposeUiNode.Companion.getSetModifier());
            int n5 = 0xE & $changed\3 >> 6;
            void $composer\7 = $composer\1;
            boolean bl8 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\7, (int)1833054614, (String)"C72@3469L9:Box.kt#2w3rfo");
            int n6 = 6 | 0x70 & $changed\1 >> 6;
            void var32_32 = $composer\7;
            BoxScope boxScope = (BoxScope)BoxScopeInstance.INSTANCE;
            boolean bl9 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\8, (int)-408432655, (String)"C1840@86453L16:GroupActivity.kt#83vr7l");
            if (StringsKt.isBlank((CharSequence)$searchQuery)) {
                $composer\8.startReplaceGroup(-408408352);
                ComposerKt.sourceInformation((Composer)$composer\8, (String)"1835@86157L43,1834@86100L278");
                TextKt.Text--4IGK_g((String)StringResources_androidKt.stringResource((int)R.string.search_placeholder, (Composer)$composer\8, (int)0), null, (long)$palette.getTextTertiary-0d7_KjU(), (long)TextUnitKt.getSp((int)14), null, null, null, (long)0L, null, null, (long)0L, (int)0, (boolean)false, (int)0, (int)0, null, null, (Composer)$composer\8, (int)3072, (int)0, (int)131058);
                $composer\8.endReplaceGroup();
            } else {
                $composer\8.startReplaceGroup(-493789978);
                $composer\8.endReplaceGroup();
            }
            innerTextField.invoke((Object)$composer\8, (Object)(0xE & $dirty));
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\8);
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\7);
            $composer\1.endNode();
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\1);
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\1);
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\1);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        } else {
            $composer.skipToGroupEnd();
        }
        return Unit.INSTANCE;
    }

    private static final Unit GroupAddExistingMemorySheet$lambda$331$lambda$330$lambda$329$lambda$328$lambda$320$lambda$319$lambda$317$lambda$316(Function1 $onSearchQueryChange) {
        $onSearchQueryChange.invoke((Object)"");
        return Unit.INSTANCE;
    }

    @Composable
    @ComposableTarget(applier="androidx.compose.ui.UiComposable")
    private static final Unit GroupAddExistingMemorySheet$lambda$331$lambda$330$lambda$329$lambda$328$lambda$320$lambda$319$lambda$318(NoMemoPalette $palette, BoxScope $this$PressScaleBox, Composer $composer, int $changed) {
        Intrinsics.checkNotNullParameter((Object)$this$PressScaleBox, (String)"$this$PressScaleBox");
        ComposerKt.sourceInformation((Composer)$composer, (String)"C1848@86843L42,1849@86948L31,1847@86787L372:GroupActivity.kt#83vr7l");
        if ($composer.shouldExecute(($changed & 0x11) != 16, $changed & 1)) {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart((int)-992493914, (int)$changed, (int)-1, (String)"com.han.nomemo.GroupActivity.GroupAddExistingMemorySheet.<anonymous>.<anonymous>.<anonymous>.<anonymous>.<anonymous>.<anonymous>.<anonymous> (GroupActivity.kt:1847)");
            }
            Painter painter = PainterResources_androidKt.painterResource((int)R.drawable.ic_sheet_close, (Composer)$composer, (int)0);
            String string2 = StringResources_androidKt.stringResource((int)R.string.cancel, (Composer)$composer, (int)0);
            long l = $palette.getTextTertiary-0d7_KjU();
            int n = 16;
            boolean bl = false;
            Modifier modifier = SizeKt.size-3ABfNKs((Modifier)((Modifier)Modifier.Companion), (float)Dp.constructor-impl((float)n));
            IconKt.Icon-ww6aTOc((Painter)painter, (String)string2, (Modifier)modifier, (long)l, (Composer)$composer, (int)384, (int)0);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        } else {
            $composer.skipToGroupEnd();
        }
        return Unit.INSTANCE;
    }

    /*
     * WARNING - void declaration
     */
    @Composable
    @ComposableTarget(applier="androidx.compose.ui.UiComposable")
    private static final Unit GroupAddExistingMemorySheet$lambda$331$lambda$330$lambda$329$lambda$328$lambda$320(NoMemoPalette $palette, String $searchQuery, Function1 $onSearchQueryChange, ColumnScope $this$Card, Composer $composer, int $changed) {
        Intrinsics.checkNotNullParameter((Object)$this$Card, (String)"$this$Card");
        ComposerKt.sourceInformation((Composer)$composer, (String)"C1804@84458L2791:GroupActivity.kt#83vr7l");
        if ($composer.shouldExecute(($changed & 0x11) != 16, $changed & 1)) {
            void $composer\10;
            void $changed\5;
            void $changed\4;
            void modifier\4;
            void modifier\3;
            void $changed\3;
            void verticalAlignment\3;
            void $composer\3;
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart((int)611685411, (int)$changed, (int)-1, (String)"com.han.nomemo.GroupActivity.GroupAddExistingMemorySheet.<anonymous>.<anonymous>.<anonymous>.<anonymous>.<anonymous> (GroupActivity.kt:1804)");
            }
            int n = 56;
            boolean bl = false;
            int $this$dp\32 = 14;
            boolean bl2 = false;
            Modifier $this$dp\32 = PaddingKt.padding-VpY3zN4$default((Modifier)SizeKt.height-3ABfNKs((Modifier)SizeKt.fillMaxWidth$default((Modifier)((Modifier)Modifier.Companion), (float)0.0f, (int)1, null), (float)Dp.constructor-impl((float)n)), (float)Dp.constructor-impl((float)$this$dp\32), (float)0.0f, (int)2, null);
            Alignment.Vertical vertical = Alignment.Companion.getCenterVertically();
            Composer composer = $composer;
            int n2 = 390;
            boolean bl3 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\3, (int)844473419, (String)"CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo");
            Arrangement.Horizontal horizontal = Arrangement.INSTANCE.getStart();
            MeasurePolicy measurePolicy = RowKt.rowMeasurePolicy((Arrangement.Horizontal)horizontal, (Alignment.Vertical)verticalAlignment\3, (Composer)$composer\3, (int)(0xE & $changed\3 >> 3 | 0x70 & $changed\3 >> 3));
            void var13_15 = modifier\3;
            int n3 = 0x70 & $changed\3 << 3;
            boolean bl4 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\3, (int)-1159599143, (String)"CC(Layout)P(!1,2)80@3267L27,83@3433L360:Layout.kt#80mrfh");
            int n4 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode((Composer)$composer\3, (int)0));
            CompositionLocalMap compositionLocalMap = $composer\3.getCurrentCompositionLocalMap();
            Modifier modifier = ComposedModifierKt.materializeModifier((Composer)$composer\3, (Modifier)modifier\4);
            Function0 function0 = ComposeUiNode.Companion.getConstructor();
            int n5 = 6 | 0x380 & $changed\4 << 6;
            boolean bl5 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\3, (int)-553112988, (String)"CC(ReusableComposeNode)N(factory,update,content)399@15590L9:Composables.kt#9igjgp");
            if (!($composer\3.getApplier() instanceof Applier)) {
                ComposablesKt.invalidApplier();
            }
            $composer\3.startReusableNode();
            if ($composer\3.getInserting()) {
                void factory\5;
                $composer\3.createNode((Function0)factory\5);
            } else {
                $composer\3.useNode();
            }
            Composer composer2 = Updater.constructor-impl((Composer)$composer\3);
            boolean bl6 = false;
            Updater.set-impl((Composer)composer2, (Object)measurePolicy, (Function2)ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.set-impl((Composer)composer2, (Object)compositionLocalMap, (Function2)ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            Function2 function2 = ComposeUiNode.Companion.getSetCompositeKeyHash();
            boolean bl7 = false;
            Composer composer3 = composer2;
            boolean bl8 = false;
            if (composer3.getInserting() || !Intrinsics.areEqual((Object)composer3.rememberedValue(), (Object)n4)) {
                composer3.updateRememberedValue((Object)n4);
                composer2.apply((Object)n4, function2);
            }
            Updater.set-impl((Composer)composer2, (Object)modifier, (Function2)ComposeUiNode.Companion.getSetModifier());
            int n6 = 0xE & $changed\5 >> 6;
            void $composer\9 = $composer\3;
            boolean bl9 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\9, (int)1456264949, (String)"C101@5233L9:Row.kt#2w3rfo");
            int n7 = 6 | 0x70 & $changed\3 >> 6;
            void var32_34 = $composer\9;
            RowScope rowScope = (RowScope)RowScopeInstance.INSTANCE;
            boolean bl10 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\10, (int)-914798804, (String)"C1812@84847L40,1813@84942L38,1811@84799L338,1828@85753L780,1817@85166L1367:GroupActivity.kt#83vr7l");
            Painter painter = PainterResources_androidKt.painterResource((int)R.drawable.ic_nm_search, (Composer)$composer\10, (int)0);
            String string2 = StringResources_androidKt.stringResource((int)R.string.action_search, (Composer)$composer\10, (int)0);
            long l = $palette.getTextSecondary-0d7_KjU();
            int n8 = 18;
            boolean bl11 = false;
            Modifier modifier2 = SizeKt.size-3ABfNKs((Modifier)((Modifier)Modifier.Companion), (float)Dp.constructor-impl((float)n8));
            IconKt.Icon-ww6aTOc((Painter)painter, (String)string2, (Modifier)modifier2, (long)l, (Composer)$composer\10, (int)384, (int)0);
            painter = new TextStyle($palette.getTextPrimary-0d7_KjU(), TextUnitKt.getSp((int)15), null, null, null, null, null, 0L, null, null, null, 0L, null, null, null, 0, 0, 0L, null, null, null, 0, 0, null, 0xFFFFFC, null);
            int n9 = 10;
            boolean bl12 = false;
            string2 = PaddingKt.padding-qDBjuR0$default((Modifier)RowScope.weight$default((RowScope)rowScope, (Modifier)((Modifier)Modifier.Companion), (float)1.0f, (boolean)false, (int)2, null), (float)Dp.constructor-impl((float)n9), (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null);
            BasicTextFieldKt.BasicTextField((String)$searchQuery, (Function1)$onSearchQueryChange, (Modifier)string2, (boolean)false, (boolean)false, (TextStyle)painter, null, null, (boolean)true, (int)0, (int)0, null, null, null, null, (Function3)((Function3)ComposableLambdaKt.rememberComposableLambda((int)-602975070, (boolean)true, (arg_0, arg_1, arg_2) -> GroupActivity.GroupAddExistingMemorySheet$lambda$331$lambda$330$lambda$329$lambda$328$lambda$320$lambda$319$lambda$315($searchQuery, $palette, arg_0, arg_1, arg_2), (Composer)$composer\10, (int)54)), (Composer)$composer\10, (int)0x6000000, (int)196608, (int)32472);
            if (!StringsKt.isBlank((CharSequence)$searchQuery)) {
                Object object;
                void $this$cache\13;
                $composer\10.startReplaceGroup(-913075732);
                ComposerKt.sourceInformation((Composer)$composer\10, (String)"1845@86687L27,1846@86749L444,1844@86626L567");
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\10, (int)2048758362, (String)"CC(remember):GroupActivity.kt#9igjgp");
                string2 = $composer\10;
                boolean bl13 = $composer\10.changed((Object)$onSearchQueryChange);
                boolean bl14 = false;
                Object object2 = $this$cache\13.rememberedValue();
                boolean bl15 = false;
                if (bl13 || object2 == Composer.Companion.getEmpty()) {
                    boolean bl16 = false;
                    Function0 function02 = () -> GroupActivity.GroupAddExistingMemorySheet$lambda$331$lambda$330$lambda$329$lambda$328$lambda$320$lambda$319$lambda$317$lambda$316($onSearchQueryChange);
                    $this$cache\13.updateRememberedValue((Object)function02);
                    object = function02;
                } else {
                    object = object2;
                }
                painter = (Function0)object;
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\10);
                ComposeUiKt.PressScaleBox((Function0<Unit>)painter, null, 0.0f, null, (Function3<? super BoxScope, ? super Composer, ? super Integer, Unit>)((Function3)ComposableLambdaKt.rememberComposableLambda((int)-992493914, (boolean)true, (arg_0, arg_1, arg_2) -> GroupActivity.GroupAddExistingMemorySheet$lambda$331$lambda$330$lambda$329$lambda$328$lambda$320$lambda$319$lambda$318($palette, arg_0, arg_1, arg_2), (Composer)$composer\10, (int)54)), (Composer)$composer\10, 24576, 14);
                $composer\10.endReplaceGroup();
            } else {
                $composer\10.startReplaceGroup(-998995549);
                $composer\10.endReplaceGroup();
            }
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\10);
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\9);
            $composer\3.endNode();
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\3);
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\3);
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\3);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        } else {
            $composer.skipToGroupEnd();
        }
        return Unit.INSTANCE;
    }

    private static final Object GroupAddExistingMemorySheet$lambda$331$lambda$330$lambda$329$lambda$328$lambda$327$lambda$326$lambda$322(MemoryRecord it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        String string2 = it.getRecordId();
        Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"getRecordId(...)");
        return string2;
    }

    /*
     * WARNING - void declaration
     */
    private static final Unit GroupAddExistingMemorySheet$lambda$331$lambda$330$lambda$329$lambda$328$lambda$327$lambda$326(List $records, Set $selectedRecordIds, Function1 $onToggleRecord, NoMemoPalette $palette, NoMemoAdaptiveSpec $adaptive, LazyListScope $this$LazyColumn) {
        void items\1;
        void $this$items_u24default\1;
        Intrinsics.checkNotNullParameter((Object)$this$LazyColumn, (String)"$this$LazyColumn");
        LazyListScope lazyListScope = $this$LazyColumn;
        List list = $records;
        Function1 function1 = GroupActivity::GroupAddExistingMemorySheet$lambda$331$lambda$330$lambda$329$lambda$328$lambda$327$lambda$326$lambda$322;
        Function1 function12 = GroupAddExistingMemorySheet$lambda$331$lambda$330$lambda$329$lambda$328$lambda$327$lambda$326$$inlined$items$default$1.INSTANCE;
        boolean bl = false;
        $this$items_u24default\1.items(items\1.size(), function1 != null ? (Function1)new Function1<Integer, Object>(function1, (List)items\1){
            final /* synthetic */ Function1 $key;
            final /* synthetic */ List $items;
            {
                this.$key = $key;
                this.$items = $items;
            }

            public final Object invoke(int index) {
                return this.$key.invoke(this.$items.get(index));
            }
        } : null, (Function1)new Function1<Integer, Object>(function12, (List)items\1){
            final /* synthetic */ Function1 $contentType;
            final /* synthetic */ List $items;
            {
                this.$contentType = $contentType;
                this.$items = $items;
            }

            public final Object invoke(int index) {
                return this.$contentType.invoke(this.$items.get(index));
            }
        }, (Function4)ComposableLambdaKt.composableLambdaInstance((int)802480018, (boolean)true, (Object)new Function4<LazyItemScope, Integer, Composer, Integer, Unit>((List)items\1, $selectedRecordIds, $onToggleRecord, $palette, $adaptive){
            final /* synthetic */ List $items;
            final /* synthetic */ Set $selectedRecordIds$inlined;
            final /* synthetic */ Function1 $onToggleRecord$inlined;
            final /* synthetic */ NoMemoPalette $palette$inlined;
            final /* synthetic */ NoMemoAdaptiveSpec $adaptive$inlined;
            {
                this.$items = $items;
                this.$selectedRecordIds$inlined = set;
                this.$onToggleRecord$inlined = function1;
                this.$palette$inlined = noMemoPalette;
                this.$adaptive$inlined = noMemoAdaptiveSpec;
            }

            /*
             * WARNING - void declaration
             */
            @Composable
            public final void invoke(LazyItemScope $this$items, int it, Composer $composer, int $changed) {
                ComposerKt.sourceInformation((Composer)$composer, (String)"CN(it)178@8834L22:LazyDsl.kt#428nma");
                int $dirty = $changed;
                if (($changed & 6) == 0) {
                    $dirty |= $composer.changed((Object)$this$items) ? 4 : 2;
                }
                if (($changed & 0x30) == 0) {
                    $dirty |= $composer.changed(it) ? 32 : 16;
                }
                if ($composer.shouldExecute(($dirty & 0x93) != 146, $dirty & 1)) {
                    void $changed\1;
                    Object object;
                    void $this$cache\5;
                    Object object2;
                    Function0 function0;
                    void var19_19;
                    Modifier modifier;
                    boolean bl;
                    void $this$cache\2;
                    void record\1;
                    void $composer\1;
                    if (ComposerKt.isTraceInProgress()) {
                        ComposerKt.traceEventStart((int)802480018, (int)$dirty, (int)-1, (String)"androidx.compose.foundation.lazy.items.<anonymous> (LazyDsl.kt:178)");
                    }
                    int n = 0xE & $dirty;
                    Composer composer = $composer;
                    MemoryRecord memoryRecord = (MemoryRecord)this.$items.get(it);
                    LazyItemScope lazyItemScope = $this$items;
                    boolean bl2 = false;
                    $composer\1.startReplaceGroup(612686246);
                    ComposerKt.sourceInformation((Composer)$composer\1, (String)"CN(record)*1888@88846L35,1889@88933L35,1881@88448L554:GroupActivity.kt#83vr7l");
                    boolean bl3 = this.$selectedRecordIds$inlined.contains(record\1.getRecordId());
                    void v0 = record\1;
                    Modifier modifier2 = null;
                    boolean bl4 = bl3;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer\1, (int)712515771, (String)"CC(remember):GroupActivity.kt#9igjgp");
                    void var12_12 = $composer\1;
                    boolean bl5 = $composer\1.changed((Object)this.$onToggleRecord$inlined) | $composer\1.changedInstance((Object)record\1);
                    boolean bl6 = false;
                    Object object3 = $this$cache\2.rememberedValue();
                    boolean bl7 = false;
                    if (bl5 || object3 == Composer.Companion.getEmpty()) {
                        bl = bl4;
                        modifier = modifier2;
                        var19_19 = v0;
                        boolean bl8 = false;
                        function0 = (Function0)new Function0<Unit>((Function1<? super String, Unit>)this.$onToggleRecord$inlined, (MemoryRecord)record\1){
                            final /* synthetic */ Function1<String, Unit> $onToggleRecord;
                            final /* synthetic */ MemoryRecord $record;
                            {
                                this.$onToggleRecord = $onToggleRecord;
                                this.$record = $record;
                            }

                            public final void invoke() {
                                String string2 = this.$record.getRecordId();
                                Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"getRecordId(...)");
                                this.$onToggleRecord.invoke((Object)string2);
                            }
                        };
                        v0 = var19_19;
                        modifier2 = modifier;
                        bl4 = bl;
                        Function0 function02 = function0;
                        $this$cache\2.updateRememberedValue((Object)function02);
                        object2 = function02;
                    } else {
                        object2 = object3;
                    }
                    Function0 function03 = (Function0)object2;
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer\1);
                    Function0 function04 = function03;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer\1, (int)712518555, (String)"CC(remember):GroupActivity.kt#9igjgp");
                    $this$cache\2 = $composer\1;
                    boolean bl9 = $composer\1.changed((Object)this.$onToggleRecord$inlined) | $composer\1.changedInstance((Object)record\1);
                    boolean bl10 = false;
                    Object object4 = $this$cache\5.rememberedValue();
                    boolean bl11 = false;
                    if (bl9 || object4 == Composer.Companion.getEmpty()) {
                        function0 = function04;
                        bl = bl4;
                        modifier = modifier2;
                        var19_19 = v0;
                        boolean bl12 = false;
                        Function0 function05 = (Function0)new Function0<Unit>((Function1<? super String, Unit>)this.$onToggleRecord$inlined, (MemoryRecord)record\1){
                            final /* synthetic */ Function1<String, Unit> $onToggleRecord;
                            final /* synthetic */ MemoryRecord $record;
                            {
                                this.$onToggleRecord = $onToggleRecord;
                                this.$record = $record;
                            }

                            public final void invoke() {
                                String string2 = this.$record.getRecordId();
                                Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"getRecordId(...)");
                                this.$onToggleRecord.invoke((Object)string2);
                            }
                        };
                        v0 = var19_19;
                        modifier2 = modifier;
                        bl4 = bl;
                        function04 = function0;
                        Function0 function06 = function05;
                        $this$cache\5.updateRememberedValue((Object)function06);
                        object = function06;
                    } else {
                        object = object4;
                    }
                    function03 = (Function0)object;
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer\1);
                    ComposeUiKt.RecordCard-IJOc0f0((MemoryRecord)v0, modifier2, bl4, (Function0<Unit>)function04, (Function0<Unit>)function03, this.$palette$inlined, this.$adaptive$inlined, true, false, null, null, (Composer)$composer\1, 0x6C00000 | 0xE & $changed\1 >> 3, 0, 1538);
                    $composer\1.endReplaceGroup();
                    if (ComposerKt.isTraceInProgress()) {
                        ComposerKt.traceEventEnd();
                    }
                } else {
                    $composer.skipToGroupEnd();
                }
            }
        }));
        return Unit.INSTANCE;
    }

    /*
     * WARNING - void declaration
     */
    @Composable
    @ComposableTarget(applier="androidx.compose.ui.UiComposable")
    private static final Unit GroupAddExistingMemorySheet$lambda$331$lambda$330$lambda$329(float $bodyHeight, long $dragHandleColor, NoMemoSheetDragController $sheetDrag, long $searchSurface, List $records, NoMemoAdaptiveSpec $adaptive, Set $selectedRecordIds, Function1 $onToggleRecord, NoMemoPalette $palette, Function0 $requestConfirm, Function0 $tryDismiss, String $searchQuery, Function1 $onSearchQueryChange, ColumnScope $this$Card, Composer $composer, int $changed) {
        Intrinsics.checkNotNullParameter((Object)$this$Card, (String)"$this$Card");
        ComposerKt.sourceInformation((Composer)$composer, (String)"C1751@81951L7147:GroupActivity.kt#83vr7l");
        if ($composer.shouldExecute(($changed & 0x11) != 16, $changed & 1)) {
            void $composer\34;
            void $changed\29;
            void $changed\28;
            void modifier\28;
            void modifier\27;
            void $changed\27;
            void horizontalAlignment\27;
            void $composer\27;
            Object object;
            Function0 function0;
            void $this$cache\23;
            void $composer\22;
            int n;
            Function0 function02;
            int n2;
            void modifier\16;
            void modifier\15;
            int n3;
            void verticalAlignment\15;
            void $composer\15;
            void $composer\12;
            void $changed\7;
            void $changed\6;
            void modifier\6;
            void modifier\5;
            void $changed\5;
            void $composer\5;
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart((int)1694630155, (int)$changed, (int)-1, (String)"com.han.nomemo.GroupActivity.GroupAddExistingMemorySheet.<anonymous>.<anonymous>.<anonymous> (GroupActivity.kt:1751)");
            }
            int n4 = 14;
            boolean bl = false;
            int n5 = 10;
            boolean bl2 = false;
            int n6 = 14;
            boolean bl3 = false;
            boolean $this$dp\52 = false;
            boolean bl4 = false;
            Modifier $this$dp\52 = PaddingKt.padding-qDBjuR0((Modifier)SizeKt.heightIn-VpY3zN4$default((Modifier)SizeKt.fillMaxWidth$default((Modifier)((Modifier)Modifier.Companion), (float)0.0f, (int)1, null), (float)0.0f, (float)$bodyHeight, (int)1, null), (float)Dp.constructor-impl((float)n4), (float)Dp.constructor-impl((float)n5), (float)Dp.constructor-impl((float)n6), (float)Dp.constructor-impl((float)((float)$this$dp\52)));
            Composer composer = $composer;
            boolean bl5 = false;
            boolean bl6 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\5, (int)1341605231, (String)"CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo");
            Arrangement.Vertical vertical = Arrangement.INSTANCE.getTop();
            Alignment.Horizontal horizontal = Alignment.Companion.getStart();
            MeasurePolicy measurePolicy = ColumnKt.columnMeasurePolicy((Arrangement.Vertical)vertical, (Alignment.Horizontal)horizontal, (Composer)$composer\5, (int)(0xE & $changed\5 >> 3 | 0x70 & $changed\5 >> 3));
            void var25_25 = modifier\5;
            int n7 = 0x70 & $changed\5 << 3;
            boolean bl7 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\5, (int)-1159599143, (String)"CC(Layout)P(!1,2)80@3267L27,83@3433L360:Layout.kt#80mrfh");
            int n8 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode((Composer)$composer\5, (int)0));
            CompositionLocalMap compositionLocalMap = $composer\5.getCurrentCompositionLocalMap();
            Modifier modifier = ComposedModifierKt.materializeModifier((Composer)$composer\5, (Modifier)modifier\6);
            Function0 function03 = ComposeUiNode.Companion.getConstructor();
            int n9 = 6 | 0x380 & $changed\6 << 6;
            boolean bl8 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\5, (int)-553112988, (String)"CC(ReusableComposeNode)N(factory,update,content)399@15590L9:Composables.kt#9igjgp");
            if (!($composer\5.getApplier() instanceof Applier)) {
                ComposablesKt.invalidApplier();
            }
            $composer\5.startReusableNode();
            if ($composer\5.getInserting()) {
                void factory\7;
                $composer\5.createNode((Function0)factory\7);
            } else {
                $composer\5.useNode();
            }
            Composer composer2 = Updater.constructor-impl((Composer)$composer\5);
            boolean bl9 = false;
            Updater.set-impl((Composer)composer2, (Object)measurePolicy, (Function2)ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.set-impl((Composer)composer2, (Object)compositionLocalMap, (Function2)ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            Function2 function2 = ComposeUiNode.Companion.getSetCompositeKeyHash();
            boolean bl10 = false;
            Composer composer3 = composer2;
            boolean bl11 = false;
            if (composer3.getInserting() || !Intrinsics.areEqual((Object)composer3.rememberedValue(), (Object)n8)) {
                composer3.updateRememberedValue((Object)n8);
                composer2.apply((Object)n8, function2);
            }
            Updater.set-impl((Composer)composer2, (Object)modifier, (Function2)ComposeUiNode.Companion.getSetModifier());
            int n10 = 0xE & $changed\7 >> 6;
            void $composer\11 = $composer\5;
            boolean bl12 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\11, (int)2093002350, (String)"C89@4557L9:Column.kt#2w3rfo");
            int n11 = 6 | 0x70 & $changed\5 >> 6;
            void var44_44 = $composer\11;
            ColumnScope columnScope = (ColumnScope)ColumnScopeInstance.INSTANCE;
            boolean bl13 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\12, (int)-194540906, (String)"C1757@82240L221,1763@82483L1607,1802@84367L42,1803@84432L2839,1797@84112L3159:GroupActivity.kt#83vr7l");
            ComposeUiKt.NoMemoSheetDragHandle-KTwxG1Y($dragHandleColor, $sheetDrag, columnScope.align((Modifier)Modifier.Companion, Alignment.Companion.getCenterHorizontally()), (Composer)$composer\12, 0, 0);
            int n12 = 12;
            boolean bl14 = false;
            int $this$dp\152 = 12;
            boolean bl15 = false;
            Modifier $this$dp\152 = PaddingKt.padding-qDBjuR0$default((Modifier)SizeKt.fillMaxWidth$default((Modifier)((Modifier)Modifier.Companion), (float)0.0f, (int)1, null), (float)0.0f, (float)Dp.constructor-impl((float)n12), (float)0.0f, (float)Dp.constructor-impl((float)$this$dp\152), (int)5, null);
            Alignment.Vertical vertical2 = Alignment.Companion.getCenterVertically();
            void var50_64 = $composer\12;
            int n13 = 390;
            boolean bl16 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\15, (int)844473419, (String)"CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo");
            Arrangement.Horizontal horizontal2 = Arrangement.INSTANCE.getStart();
            MeasurePolicy measurePolicy2 = RowKt.rowMeasurePolicy((Arrangement.Horizontal)horizontal2, (Alignment.Vertical)verticalAlignment\15, (Composer)$composer\15, (int)(0xE & n3 >> 3 | 0x70 & n3 >> 3));
            void var54_71 = modifier\15;
            int n14 = 0x70 & n3 << 3;
            boolean bl17 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\15, (int)-1159599143, (String)"CC(Layout)P(!1,2)80@3267L27,83@3433L360:Layout.kt#80mrfh");
            int n15 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode((Composer)$composer\15, (int)0));
            CompositionLocalMap compositionLocalMap2 = $composer\15.getCurrentCompositionLocalMap();
            Modifier modifier2 = ComposedModifierKt.materializeModifier((Composer)$composer\15, (Modifier)modifier\16);
            Function0 function04 = ComposeUiNode.Companion.getConstructor();
            int n16 = 6 | 0x380 & n2 << 6;
            boolean bl18 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\15, (int)-553112988, (String)"CC(ReusableComposeNode)N(factory,update,content)399@15590L9:Composables.kt#9igjgp");
            if (!($composer\15.getApplier() instanceof Applier)) {
                ComposablesKt.invalidApplier();
            }
            $composer\15.startReusableNode();
            if ($composer\15.getInserting()) {
                $composer\15.createNode(function02);
            } else {
                $composer\15.useNode();
            }
            Composer composer4 = Updater.constructor-impl((Composer)$composer\15);
            boolean bl19 = false;
            Updater.set-impl((Composer)composer4, (Object)measurePolicy2, (Function2)ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.set-impl((Composer)composer4, (Object)compositionLocalMap2, (Function2)ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            Function2 function22 = ComposeUiNode.Companion.getSetCompositeKeyHash();
            boolean bl20 = false;
            Composer composer5 = composer4;
            boolean bl21 = false;
            if (composer5.getInserting() || !Intrinsics.areEqual((Object)composer5.rememberedValue(), (Object)n15)) {
                composer5.updateRememberedValue((Object)n15);
                composer4.apply((Object)n15, function22);
            }
            Updater.set-impl((Composer)composer4, (Object)modifier2, (Function2)ComposeUiNode.Companion.getSetModifier());
            int n17 = 0xE & n >> 6;
            void $composer\21 = $composer\15;
            boolean bl22 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\21, (int)1456264949, (String)"C101@5233L9:Row.kt#2w3rfo");
            int n18 = 6 | 0x70 & n3 >> 6;
            void var73_90 = $composer\21;
            RowScope rowScope = (RowScope)RowScopeInstance.INSTANCE;
            boolean bl23 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\22, (int)1530099032, (String)"C1771@82899L31,1772@82970L16,1769@82762L315,1775@83102L627,1791@83891L32,1789@83754L314:GroupActivity.kt#83vr7l");
            int n19 = R.drawable.ic_sheet_close;
            String string2 = StringResources_androidKt.stringResource((int)R.string.cancel, (Composer)$composer\22, (int)0);
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\22, (int)1989026049, (String)"CC(remember):GroupActivity.kt#9igjgp");
            void var76_93 = $composer\22;
            boolean invalid\242 = false;
            boolean $i$f$cache\23\17742 = false;
            Object it\242 = $this$cache\23.rememberedValue();
            boolean bl24 = false;
            if (it\242 == Composer.Companion.getEmpty()) {
                String string3 = string2;
                int n20 = n19;
                boolean bl25 = false;
                Function0 function05 = () -> GroupActivity.GroupAddExistingMemorySheet$lambda$331$lambda$330$lambda$329$lambda$328$lambda$313$lambda$311$lambda$310($tryDismiss);
                n19 = n20;
                string2 = string3;
                function0 = function05;
                $this$cache\23.updateRememberedValue((Object)function0);
                object = function0;
            } else {
                object = it\242;
            }
            Function0 function06 = (Function0)object;
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\22);
            ComposeUiKt.GlassIconCircleButton-WH-ejsw(n19, string2, (Function0<Unit>)function06, null, $adaptive.getTopActionButtonSize-D9Ej5fM(), null, (Composer)$composer\22, 384, 40);
            int $this$dp\272 = 10;
            boolean bl26 = false;
            Modifier $this$dp\272 = PaddingKt.padding-VpY3zN4$default((Modifier)RowScope.weight$default((RowScope)rowScope, (Modifier)((Modifier)Modifier.Companion), (float)1.0f, (boolean)false, (int)2, null), (float)Dp.constructor-impl((float)$this$dp\272), (float)0.0f, (int)2, null);
            Alignment.Horizontal invalid\242 = Alignment.Companion.getCenterHorizontally();
            void $i$f$cache\23\17742 = $composer\22;
            int it\242 = 384;
            boolean bl27 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\27, (int)1341605231, (String)"CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo");
            Arrangement.Vertical vertical3 = Arrangement.INSTANCE.getTop();
            MeasurePolicy measurePolicy3 = ColumnKt.columnMeasurePolicy((Arrangement.Vertical)vertical3, (Alignment.Horizontal)horizontalAlignment\27, (Composer)$composer\27, (int)(0xE & $changed\27 >> 3 | 0x70 & $changed\27 >> 3));
            function0 = modifier\27;
            int n21 = 0x70 & $changed\27 << 3;
            boolean bl28 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\27, (int)-1159599143, (String)"CC(Layout)P(!1,2)80@3267L27,83@3433L360:Layout.kt#80mrfh");
            int n22 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode((Composer)$composer\27, (int)0));
            CompositionLocalMap compositionLocalMap3 = $composer\27.getCurrentCompositionLocalMap();
            Modifier modifier3 = ComposedModifierKt.materializeModifier((Composer)$composer\27, (Modifier)modifier\28);
            Function0 function07 = ComposeUiNode.Companion.getConstructor();
            int n23 = 6 | 0x380 & $changed\28 << 6;
            boolean bl29 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\27, (int)-553112988, (String)"CC(ReusableComposeNode)N(factory,update,content)399@15590L9:Composables.kt#9igjgp");
            if (!($composer\27.getApplier() instanceof Applier)) {
                ComposablesKt.invalidApplier();
            }
            $composer\27.startReusableNode();
            if ($composer\27.getInserting()) {
                void factory\29;
                $composer\27.createNode((Function0)factory\29);
            } else {
                $composer\27.useNode();
            }
            Composer composer6 = Updater.constructor-impl((Composer)$composer\27);
            boolean bl30 = false;
            Updater.set-impl((Composer)composer6, (Object)measurePolicy3, (Function2)ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.set-impl((Composer)composer6, (Object)compositionLocalMap3, (Function2)ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            Function2 function23 = ComposeUiNode.Companion.getSetCompositeKeyHash();
            boolean bl31 = false;
            Composer composer7 = composer6;
            boolean bl32 = false;
            if (composer7.getInserting() || !Intrinsics.areEqual((Object)composer7.rememberedValue(), (Object)n22)) {
                composer7.updateRememberedValue((Object)n22);
                composer6.apply((Object)n22, function23);
            }
            Updater.set-impl((Composer)composer6, (Object)modifier3, (Function2)ComposeUiNode.Companion.getSetModifier());
            int n24 = 0xE & $changed\29 >> 6;
            void $composer\33 = $composer\27;
            boolean bl33 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\33, (int)2093002350, (String)"C89@4557L9:Column.kt#2w3rfo");
            int n25 = 6 | 0x70 & $changed\27 >> 6;
            void var105_130 = $composer\33;
            ColumnScope columnScope2 = (ColumnScope)ColumnScopeInstance.INSTANCE;
            boolean bl34 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\34, (int)1386412024, (String)"C1781@83399L304:GroupActivity.kt#83vr7l");
            TextKt.Text--4IGK_g((String)"\u6dfb\u52a0\u8bb0\u5fc6", null, (long)$palette.getTextPrimary-0d7_KjU(), (long)TextUnitKt.getSp((int)18), null, (FontWeight)FontWeight.Companion.getSemiBold(), null, (long)0L, null, null, (long)0L, (int)0, (boolean)false, (int)1, (int)0, null, null, (Composer)$composer\34, (int)199686, (int)3072, (int)122834);
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\34);
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\33);
            $composer\27.endNode();
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\27);
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\27);
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\27);
            ComposeUiKt.GlassIconCircleButton-WH-ejsw(R.drawable.ic_sheet_check, StringResources_androidKt.stringResource((int)R.string.confirm, (Composer)$composer\22, (int)0), (Function0<Unit>)$requestConfirm, null, $adaptive.getTopActionButtonSize-D9Ej5fM(), null, (Composer)$composer\22, 0, 40);
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\22);
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\21);
            $composer\15.endNode();
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\15);
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\15);
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\15);
            int n26 = 12;
            boolean bl35 = false;
            int n272 = 22;
            boolean bl362 = false;
            CardKt.Card((Modifier)PaddingKt.padding-qDBjuR0$default((Modifier)SizeKt.fillMaxWidth$default((Modifier)((Modifier)Modifier.Companion), (float)0.0f, (int)1, null), (float)0.0f, (float)0.0f, (float)0.0f, (float)Dp.constructor-impl((float)n26), (int)7, null), (Shape)((Shape)ComposeUiKt.noMemoG2RoundedShape-0680j_4(Dp.constructor-impl((float)n272))), (CardColors)CardDefaults.INSTANCE.cardColors-ro_MJ88($searchSurface, 0L, 0L, 0L, (Composer)$composer\12, CardDefaults.$stable << 12, 14), null, null, (Function3)((Function3)ComposableLambdaKt.rememberComposableLambda((int)611685411, (boolean)true, (arg_0, arg_1, arg_2) -> GroupActivity.GroupAddExistingMemorySheet$lambda$331$lambda$330$lambda$329$lambda$328$lambda$320($palette, $searchQuery, $onSearchQueryChange, arg_0, arg_1, arg_2), (Composer)$composer\12, (int)54)), (Composer)$composer\12, (int)196614, (int)24);
            if ($records.isEmpty()) {
                void $composer\44;
                void $changed\39;
                void $changed\38;
                void modifier\38;
                void $changed\37;
                void modifier\37;
                void contentAlignment\37;
                void $composer\37;
                $composer\12.startReplaceGroup(-189701745);
                ComposerKt.sourceInformation((Composer)$composer\12, (String)"1859@87342L462");
                Modifier n272 = ColumnScope.weight$default((ColumnScope)columnScope, (Modifier)SizeKt.fillMaxWidth$default((Modifier)((Modifier)Modifier.Companion), (float)0.0f, (int)1, null), (float)1.0f, (boolean)false, (int)2, null);
                Alignment bl362 = Alignment.Companion.getCenter();
                $composer\15 = $composer\12;
                n3 = 48;
                boolean bl37 = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\37, (int)1042775818, (String)"CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo");
                boolean bl38 = false;
                MeasurePolicy measurePolicy4 = BoxKt.maybeCachedBoxMeasurePolicy((Alignment)contentAlignment\37, (boolean)bl38);
                modifier\16 = modifier\37;
                n2 = 0x70 & $changed\37 << 3;
                boolean bl39 = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\37, (int)-1159599143, (String)"CC(Layout)P(!1,2)80@3267L27,83@3433L360:Layout.kt#80mrfh");
                int n28 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode((Composer)$composer\37, (int)0));
                CompositionLocalMap compositionLocalMap4 = $composer\37.getCurrentCompositionLocalMap();
                Modifier modifier4 = ComposedModifierKt.materializeModifier((Composer)$composer\37, (Modifier)modifier\38);
                function02 = ComposeUiNode.Companion.getConstructor();
                n = 6 | 0x380 & $changed\38 << 6;
                boolean bl40 = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\37, (int)-553112988, (String)"CC(ReusableComposeNode)N(factory,update,content)399@15590L9:Composables.kt#9igjgp");
                if (!($composer\37.getApplier() instanceof Applier)) {
                    ComposablesKt.invalidApplier();
                }
                $composer\37.startReusableNode();
                if ($composer\37.getInserting()) {
                    void factory\39;
                    $composer\37.createNode((Function0)factory\39);
                } else {
                    $composer\37.useNode();
                }
                Composer composer8 = Updater.constructor-impl((Composer)$composer\37);
                boolean bl41 = false;
                Updater.set-impl((Composer)composer8, (Object)measurePolicy4, (Function2)ComposeUiNode.Companion.getSetMeasurePolicy());
                Updater.set-impl((Composer)composer8, (Object)compositionLocalMap4, (Function2)ComposeUiNode.Companion.getSetResolvedCompositionLocals());
                Function2 function24 = ComposeUiNode.Companion.getSetCompositeKeyHash();
                boolean bl42 = false;
                Composer composer9 = composer8;
                boolean bl43 = false;
                if (composer9.getInserting() || !Intrinsics.areEqual((Object)composer9.rememberedValue(), (Object)n28)) {
                    composer9.updateRememberedValue((Object)n28);
                    composer8.apply((Object)n28, function24);
                }
                Updater.set-impl((Composer)composer8, (Object)modifier4, (Function2)ComposeUiNode.Companion.getSetModifier());
                int n29 = 0xE & $changed\39 >> 6;
                void $composer\43 = $composer\37;
                boolean bl44 = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\43, (int)1833054614, (String)"C72@3469L9:Box.kt#2w3rfo");
                int n30 = 6 | 0x70 & $changed\37 >> 6;
                $composer\22 = $composer\43;
                BoxScope boxScope = (BoxScope)BoxScopeInstance.INSTANCE;
                boolean bl45 = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\44, (int)472872069, (String)"C1865@87608L170:GroupActivity.kt#83vr7l");
                ComposeUiKt.NoMemoEmptyState(R.drawable.ic_nm_memory_dock, "\u6682\u65e0\u53ef\u6dfb\u52a0\u7684\u8bb0\u5fc6", null, null, (Composer)$composer\44, 48, 12);
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\44);
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\43);
                $composer\37.endNode();
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\37);
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\37);
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\37);
                $composer\12.endReplaceGroup();
            } else {
                Object object2;
                void $this$cache\49;
                void other\47;
                void arg0\47;
                $composer\12.startReplaceGroup(-189166995);
                ComposerKt.sourceInformation((Composer)$composer\12, (String)"1878@88245L813,1871@87858L1200");
                Modifier modifier5 = ColumnScope.weight$default((ColumnScope)columnScope, (Modifier)((Modifier)Modifier.Companion), (float)1.0f, (boolean)false, (int)2, null);
                LazyListState lazyListState = null;
                boolean $this$dp\472 = false;
                boolean bl46 = false;
                float $this$dp\472 = $adaptive.getPageBottomPadding-D9Ej5fM();
                int $this$dp\492 = 18;
                boolean bl47 = false;
                float $this$dp\492 = Dp.constructor-impl((float)$this$dp\492);
                boolean bl48 = false;
                PaddingValues paddingValues = PaddingKt.PaddingValues-a9UjIt4$default((float)0.0f, (float)Dp.constructor-impl((float)((float)$this$dp\472)), (float)0.0f, (float)Dp.constructor-impl((float)(arg0\47 + other\47)), (int)5, null);
                boolean bl49 = false;
                int n31 = 12;
                boolean $i$f$getDp\48\18792 = false;
                Arrangement.Vertical vertical4 = (Arrangement.Vertical)Arrangement.INSTANCE.spacedBy-0680j_4(Dp.constructor-impl((float)n31));
                Alignment.Horizontal horizontal3 = null;
                FlingBehavior flingBehavior = null;
                boolean bl50 = false;
                OverscrollEffect overscrollEffect = null;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer\12, (int)-1945752030, (String)"CC(remember):GroupActivity.kt#9igjgp");
                void $i$f$getDp\48\18792 = $composer\12;
                boolean bl51 = $composer\12.changedInstance((Object)$records) | $composer\12.changedInstance((Object)$selectedRecordIds) | $composer\12.changed((Object)$onToggleRecord) | $composer\12.changed((Object)$palette) | $composer\12.changed((Object)$adaptive);
                boolean bl52 = false;
                Object object3 = $this$cache\49.rememberedValue();
                boolean bl53 = false;
                if (bl51 || object3 == Composer.Companion.getEmpty()) {
                    OverscrollEffect overscrollEffect2 = overscrollEffect;
                    boolean bl54 = bl50;
                    FlingBehavior flingBehavior2 = flingBehavior;
                    Alignment.Horizontal horizontal4 = horizontal3;
                    Arrangement.Vertical vertical5 = vertical4;
                    boolean bl55 = bl49;
                    PaddingValues paddingValues2 = paddingValues;
                    LazyListState lazyListState2 = lazyListState;
                    Modifier modifier6 = modifier5;
                    boolean bl56 = false;
                    Function1 function1 = arg_0 -> GroupActivity.GroupAddExistingMemorySheet$lambda$331$lambda$330$lambda$329$lambda$328$lambda$327$lambda$326($records, $selectedRecordIds, $onToggleRecord, $palette, $adaptive, arg_0);
                    modifier5 = modifier6;
                    lazyListState = lazyListState2;
                    paddingValues = paddingValues2;
                    bl49 = bl55;
                    vertical4 = vertical5;
                    horizontal3 = horizontal4;
                    flingBehavior = flingBehavior2;
                    bl50 = bl54;
                    overscrollEffect = overscrollEffect2;
                    Function1 function12 = function1;
                    $this$cache\49.updateRememberedValue((Object)function12);
                    object2 = function12;
                } else {
                    object2 = object3;
                }
                Function1 function1 = (Function1)object2;
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer\12);
                LazyDslKt.LazyColumn((Modifier)modifier5, lazyListState, (PaddingValues)paddingValues, (boolean)bl49, (Arrangement.Vertical)vertical4, horizontal3, flingBehavior, (boolean)bl50, overscrollEffect, (Function1)function1, (Composer)$composer\12, (int)24576, (int)490);
                $composer\12.endReplaceGroup();
            }
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\12);
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\11);
            $composer\5.endNode();
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\5);
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\5);
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\5);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        } else {
            $composer.skipToGroupEnd();
        }
        return Unit.INSTANCE;
    }

    @Composable
    @ComposableTarget(applier="androidx.compose.ui.UiComposable")
    private static final Unit GroupAddExistingMemorySheet$lambda$331$lambda$330(NoMemoSheetDragController $sheetDrag, NoMemoAdaptiveSpec $adaptive, long $panelSurface, float $bodyHeight, long $dragHandleColor, long $searchSurface, List $records, Set $selectedRecordIds, Function1 $onToggleRecord, NoMemoPalette $palette, Function0 $requestConfirm, Function0 $tryDismiss, String $searchQuery, Function1 $onSearchQueryChange, AnimatedVisibilityScope $this$AnimatedVisibility, Composer $composer, int $changed) {
        float f;
        Intrinsics.checkNotNullParameter((Object)$this$AnimatedVisibility, (String)"$this$AnimatedVisibility");
        ComposerKt.sourceInformation((Composer)$composer, (String)"C1749@81869L41,1750@81929L7183,1740@81375L7737:GroupActivity.kt#83vr7l");
        if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart((int)-557691587, (int)$changed, (int)-1, (String)"com.han.nomemo.GroupActivity.GroupAddExistingMemorySheet.<anonymous>.<anonymous> (GroupActivity.kt:1740)");
        }
        Modifier modifier = SizeKt.fillMaxWidth$default((Modifier)ComposeUiKt.noMemoSheetDragOffset((Modifier)Modifier.Companion, $sheetDrag), (float)0.0f, (int)1, null);
        if ($adaptive.isNarrow()) {
            int n = 18;
            boolean bl = false;
            f = Dp.constructor-impl((float)n);
        } else {
            int n = 24;
            boolean bl = false;
            f = Dp.constructor-impl((float)n);
        }
        int n = 36;
        boolean bl = false;
        int n2 = 36;
        boolean bl2 = false;
        int n3 = 36;
        boolean bl3 = false;
        int n4 = 36;
        boolean bl4 = false;
        CardKt.Card((Modifier)ShadowKt.shadow-s4CzXII$default((Modifier)modifier, (float)f, (Shape)((Shape)ComposeUiKt.noMemoG2RoundedShape-a9UjIt4$default(Dp.constructor-impl((float)n), Dp.constructor-impl((float)n2), 0.0f, 0.0f, 12, null)), (boolean)false, (long)0L, (long)0L, (int)28, null), (Shape)((Shape)ComposeUiKt.noMemoG2RoundedShape-a9UjIt4$default(Dp.constructor-impl((float)n3), Dp.constructor-impl((float)n4), 0.0f, 0.0f, 12, null)), (CardColors)CardDefaults.INSTANCE.cardColors-ro_MJ88($panelSurface, 0L, 0L, 0L, $composer, CardDefaults.$stable << 12, 14), null, null, (Function3)((Function3)ComposableLambdaKt.rememberComposableLambda((int)1694630155, (boolean)true, (arg_0, arg_1, arg_2) -> GroupActivity.GroupAddExistingMemorySheet$lambda$331$lambda$330$lambda$329($bodyHeight, $dragHandleColor, $sheetDrag, $searchSurface, $records, $adaptive, $selectedRecordIds, $onToggleRecord, $palette, $requestConfirm, $tryDismiss, $searchQuery, $onSearchQueryChange, arg_0, arg_1, arg_2), (Composer)$composer, (int)54)), (Composer)$composer, (int)196608, (int)24);
        if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
        }
        return Unit.INSTANCE;
    }

    private static final Unit GroupAddExistingMemorySheet$lambda$332(GroupActivity $tmp7_rcvr, BoxScope $this_GroupAddExistingMemorySheet, List $records, Set $selectedRecordIds, String $searchQuery, Function1 $onSearchQueryChange, Function1 $onToggleRecord, Function0 $onDismiss, Function0 $onConfirm, int $$changed, Composer $composer, int $force) {
        $tmp7_rcvr.GroupAddExistingMemorySheet($this_GroupAddExistingMemorySheet, $records, $selectedRecordIds, $searchQuery, (Function1<? super String, Unit>)$onSearchQueryChange, (Function1<? super String, Unit>)$onToggleRecord, (Function0<Unit>)$onDismiss, (Function0<Boolean>)$onConfirm, $composer, RecomposeScopeImplKt.updateChangedFlags((int)($$changed | 1)));
        return Unit.INSTANCE;
    }

    /*
     * WARNING - void declaration
     */
    private static final boolean GroupEditAlbumSheet$lambda$335(MutableState<Boolean> $visible$delegate) {
        void $this$getValue\1;
        State state = (State)$visible$delegate;
        Object var2_2 = null;
        Object property\1 = null;
        boolean bl = false;
        return (Boolean)$this$getValue\1.getValue();
    }

    /*
     * WARNING - void declaration
     */
    private static final void GroupEditAlbumSheet$lambda$336(MutableState<Boolean> $visible$delegate, boolean bl) {
        void $this$setValue\1;
        MutableState<Boolean> mutableState = $visible$delegate;
        Object var3_3 = null;
        Object var4_4 = null;
        Boolean bl2 = bl;
        boolean bl3 = false;
        $this$setValue\1.setValue((Object)bl2);
    }

    /*
     * WARNING - void declaration
     */
    private static final boolean GroupEditAlbumSheet$lambda$338(MutableState<Boolean> $dismissCommitted$delegate) {
        void $this$getValue\1;
        State state = (State)$dismissCommitted$delegate;
        Object var2_2 = null;
        Object property\1 = null;
        boolean bl = false;
        return (Boolean)$this$getValue\1.getValue();
    }

    /*
     * WARNING - void declaration
     */
    private static final void GroupEditAlbumSheet$lambda$339(MutableState<Boolean> $dismissCommitted$delegate, boolean bl) {
        void $this$setValue\1;
        MutableState<Boolean> mutableState = $dismissCommitted$delegate;
        Object var3_3 = null;
        Object var4_4 = null;
        Boolean bl2 = bl;
        boolean bl3 = false;
        $this$setValue\1.setValue((Object)bl2);
    }

    /*
     * WARNING - void declaration
     */
    private static final TextFieldValue GroupEditAlbumSheet$lambda$341(MutableState<TextFieldValue> $albumNameField$delegate) {
        void $this$getValue\1;
        State state = (State)$albumNameField$delegate;
        Object var2_2 = null;
        Object property\1 = null;
        boolean bl = false;
        return (TextFieldValue)$this$getValue\1.getValue();
    }

    /*
     * WARNING - void declaration
     */
    private static final void GroupEditAlbumSheet$lambda$342(MutableState<TextFieldValue> $albumNameField$delegate, TextFieldValue textFieldValue) {
        void $this$setValue\1;
        MutableState<TextFieldValue> mutableState = $albumNameField$delegate;
        Object var3_3 = null;
        Object var4_4 = null;
        TextFieldValue textFieldValue2 = textFieldValue;
        boolean bl = false;
        $this$setValue\1.setValue((Object)textFieldValue2);
    }

    /*
     * WARNING - void declaration
     */
    private static final TextFieldValue GroupEditAlbumSheet$lambda$344(MutableState<TextFieldValue> $albumDescriptionField$delegate) {
        void $this$getValue\1;
        State state = (State)$albumDescriptionField$delegate;
        Object var2_2 = null;
        Object property\1 = null;
        boolean bl = false;
        return (TextFieldValue)$this$getValue\1.getValue();
    }

    /*
     * WARNING - void declaration
     */
    private static final void GroupEditAlbumSheet$lambda$345(MutableState<TextFieldValue> $albumDescriptionField$delegate, TextFieldValue textFieldValue) {
        void $this$setValue\1;
        MutableState<TextFieldValue> mutableState = $albumDescriptionField$delegate;
        Object var3_3 = null;
        Object var4_4 = null;
        TextFieldValue textFieldValue2 = textFieldValue;
        boolean bl = false;
        $this$setValue\1.setValue((Object)textFieldValue2);
    }

    private static final DisposableEffectResult GroupEditAlbumSheet$lambda$350$lambda$349(Activity $activity, DisposableEffectScope $this$DisposableEffect) {
        Intrinsics.checkNotNullParameter((Object)$this$DisposableEffect, (String)"$this$DisposableEffect");
        Activity activity = $activity;
        Object window = activity != null ? activity.getWindow() : null;
        Window window2 = window;
        Integer previousSoftInputMode = window2 != null && (window2 = window2.getAttributes()) != null ? Integer.valueOf(window2.softInputMode) : null;
        Window window3 = window;
        if (window3 != null) {
            window3.setSoftInputMode(48);
        }
        DisposableEffectScope disposableEffectScope = $this$DisposableEffect;
        boolean bl = false;
        return new DisposableEffectResult((Window)window, previousSoftInputMode){
            final /* synthetic */ Window $window$inlined;
            final /* synthetic */ Integer $previousSoftInputMode$inlined;
            {
                this.$window$inlined = window;
                this.$previousSoftInputMode$inlined = n;
            }

            public void dispose() {
                boolean bl = false;
                if (this.$window$inlined != null && this.$previousSoftInputMode$inlined != null) {
                    this.$window$inlined.setSoftInputMode(this.$previousSoftInputMode$inlined.intValue());
                }
            }
        };
    }

    private static final boolean GroupEditAlbumSheet$lambda$354$lambda$353(MutableState $visible$delegate) {
        GroupActivity.GroupEditAlbumSheet$lambda$336((MutableState<Boolean>)$visible$delegate, false);
        return true;
    }

    private static final Unit GroupEditAlbumSheet$lambda$356$lambda$355(Function0 $onConfirm, MutableState $visible$delegate) {
        if (((Boolean)$onConfirm.invoke()).booleanValue()) {
            GroupActivity.GroupEditAlbumSheet$lambda$336((MutableState<Boolean>)$visible$delegate, false);
        }
        return Unit.INSTANCE;
    }

    private static final Unit GroupEditAlbumSheet$lambda$358$lambda$357(Function0 $tryDismiss) {
        $tryDismiss.invoke();
        return Unit.INSTANCE;
    }

    private static final Unit GroupEditAlbumSheet$lambda$385$lambda$362$lambda$361$lambda$360(Function0 $tryDismiss) {
        $tryDismiss.invoke();
        return Unit.INSTANCE;
    }

    /*
     * WARNING - void declaration
     */
    @Composable
    @ComposableTarget(applier="androidx.compose.ui.UiComposable")
    private static final Unit GroupEditAlbumSheet$lambda$385$lambda$362(boolean $isDark, NoMemoSheetDragController $sheetDrag, Function0 $tryDismiss, AnimatedVisibilityScope $this$AnimatedVisibility, Composer $composer, int $changed) {
        Object object;
        void $this$cache\4;
        Object object2;
        Modifier modifier;
        Composer composer;
        Intrinsics.checkNotNullParameter((Object)$this$AnimatedVisibility, (String)"$this$AnimatedVisibility");
        ComposerKt.sourceInformation((Composer)$composer, (String)"C2017@93092L39,2019@93210L16,2008@92719L543:GroupActivity.kt#83vr7l");
        if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart((int)1311448465, (int)$changed, (int)-1, (String)"com.han.nomemo.GroupActivity.GroupEditAlbumSheet.<anonymous>.<anonymous> (GroupActivity.kt:2008)");
        }
        Modifier modifier2 = BackgroundKt.background-bw27NRU$default((Modifier)SizeKt.fillMaxSize$default((Modifier)((Modifier)Modifier.Companion), (float)0.0f, (int)1, null), (long)Color.copy-wmQWz5c$default((long)Color.Companion.getBlack-0d7_KjU(), (float)(($isDark ? 0.56f : 0.28f) * $sheetDrag.getScrimAlphaFraction()), (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null), null, (int)2, null);
        ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)1876569560, (String)"CC(remember):GroupActivity.kt#9igjgp");
        Composer composer2 = $composer;
        boolean bl = false;
        boolean bl2 = false;
        Object object3 = composer.rememberedValue();
        boolean bl3 = false;
        if (object3 == Composer.Companion.getEmpty()) {
            modifier = modifier2;
            boolean bl4 = false;
            modifier2 = modifier;
            MutableInteractionSource mutableInteractionSource = InteractionSourceKt.MutableInteractionSource();
            composer.updateRememberedValue((Object)mutableInteractionSource);
            object2 = mutableInteractionSource;
        } else {
            object2 = object3;
        }
        MutableInteractionSource mutableInteractionSource = (MutableInteractionSource)object2;
        ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
        MutableInteractionSource mutableInteractionSource2 = mutableInteractionSource;
        Indication indication = null;
        boolean bl5 = false;
        String string2 = null;
        Role role = null;
        ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)1876573313, (String)"CC(remember):GroupActivity.kt#9igjgp");
        composer = $composer;
        boolean bl6 = false;
        boolean bl7 = false;
        Object object4 = $this$cache\4.rememberedValue();
        boolean bl8 = false;
        if (object4 == Composer.Companion.getEmpty()) {
            Role role2 = role;
            String string3 = string2;
            boolean bl9 = bl5;
            Indication indication2 = indication;
            MutableInteractionSource mutableInteractionSource3 = mutableInteractionSource2;
            modifier = modifier2;
            boolean bl10 = false;
            Function0 function0 = () -> GroupActivity.GroupEditAlbumSheet$lambda$385$lambda$362$lambda$361$lambda$360($tryDismiss);
            modifier2 = modifier;
            mutableInteractionSource2 = mutableInteractionSource3;
            indication = indication2;
            bl5 = bl9;
            string2 = string3;
            role = role2;
            Function0 function02 = function0;
            $this$cache\4.updateRememberedValue((Object)function02);
            object = function02;
        } else {
            object = object4;
        }
        mutableInteractionSource = (Function0)object;
        ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
        BoxKt.Box((Modifier)ClickableKt.clickable-O2vRcR0$default((Modifier)modifier2, (MutableInteractionSource)mutableInteractionSource2, indication, (boolean)bl5, string2, role, (Function0)mutableInteractionSource, (int)28, null), (Composer)$composer, (int)0);
        if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
        }
        return Unit.INSTANCE;
    }

    private static final int GroupEditAlbumSheet$lambda$385$lambda$364$lambda$363(int fullHeight) {
        return fullHeight;
    }

    private static final int GroupEditAlbumSheet$lambda$385$lambda$366$lambda$365(int fullHeight) {
        return fullHeight;
    }

    private static final Unit GroupEditAlbumSheet$lambda$385$lambda$384$lambda$383$lambda$382$lambda$370$lambda$368$lambda$367(Function0 $tryDismiss) {
        $tryDismiss.invoke();
        return Unit.INSTANCE;
    }

    private static final Unit GroupEditAlbumSheet$lambda$385$lambda$384$lambda$383$lambda$382$lambda$381$lambda$375$lambda$372$lambda$371(Function1 $onNameChange, MutableState $albumNameField$delegate, TextFieldValue updated) {
        Intrinsics.checkNotNullParameter((Object)updated, (String)"updated");
        GroupActivity.GroupEditAlbumSheet$lambda$342((MutableState<TextFieldValue>)$albumNameField$delegate, updated);
        $onNameChange.invoke((Object)updated.getText());
        return Unit.INSTANCE;
    }

    /*
     * WARNING - void declaration
     */
    @Composable
    @ComposableInferredTarget(scheme="[androidx.compose.ui.UiComposable[androidx.compose.ui.UiComposable]]")
    private static final Unit GroupEditAlbumSheet$lambda$385$lambda$384$lambda$383$lambda$382$lambda$381$lambda$375$lambda$374(NoMemoPalette $palette, MutableState $albumNameField$delegate, Function2 innerTextField, Composer $composer, int $changed) {
        Intrinsics.checkNotNullParameter((Object)innerTextField, (String)"innerTextField");
        ComposerKt.sourceInformation((Composer)$composer, (String)"CN(innerTextField)2130@98412L671:GroupActivity.kt#83vr7l");
        int $dirty = $changed;
        if (($changed & 6) == 0) {
            $dirty |= $composer.changedInstance((Object)innerTextField) ? 4 : 2;
        }
        if ($composer.shouldExecute(($dirty & 0x13) != 18, $dirty & 1)) {
            void $composer\8;
            void $changed\3;
            void $changed\2;
            void modifier\2;
            void $changed\1;
            void modifier\1;
            void contentAlignment\1;
            void $composer\1;
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart((int)1515098581, (int)$dirty, (int)-1, (String)"com.han.nomemo.GroupActivity.GroupEditAlbumSheet.<anonymous>.<anonymous>.<anonymous>.<anonymous>.<anonymous>.<anonymous>.<anonymous> (GroupActivity.kt:2130)");
            }
            Modifier modifier = SizeKt.fillMaxSize$default((Modifier)((Modifier)Modifier.Companion), (float)0.0f, (int)1, null);
            Alignment alignment = Alignment.Companion.getCenterStart();
            Composer composer = $composer;
            int n = 54;
            boolean bl = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\1, (int)1042775818, (String)"CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo");
            boolean bl2 = false;
            MeasurePolicy measurePolicy = BoxKt.maybeCachedBoxMeasurePolicy((Alignment)contentAlignment\1, (boolean)bl2);
            void var13_13 = modifier\1;
            int n2 = 0x70 & $changed\1 << 3;
            boolean bl3 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\1, (int)-1159599143, (String)"CC(Layout)P(!1,2)80@3267L27,83@3433L360:Layout.kt#80mrfh");
            int n3 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode((Composer)$composer\1, (int)0));
            CompositionLocalMap compositionLocalMap = $composer\1.getCurrentCompositionLocalMap();
            Modifier modifier2 = ComposedModifierKt.materializeModifier((Composer)$composer\1, (Modifier)modifier\2);
            Function0 function0 = ComposeUiNode.Companion.getConstructor();
            int n4 = 6 | 0x380 & $changed\2 << 6;
            boolean bl4 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\1, (int)-553112988, (String)"CC(ReusableComposeNode)N(factory,update,content)399@15590L9:Composables.kt#9igjgp");
            if (!($composer\1.getApplier() instanceof Applier)) {
                ComposablesKt.invalidApplier();
            }
            $composer\1.startReusableNode();
            if ($composer\1.getInserting()) {
                void factory\3;
                $composer\1.createNode((Function0)factory\3);
            } else {
                $composer\1.useNode();
            }
            Composer composer2 = Updater.constructor-impl((Composer)$composer\1);
            boolean bl5 = false;
            Updater.set-impl((Composer)composer2, (Object)measurePolicy, (Function2)ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.set-impl((Composer)composer2, (Object)compositionLocalMap, (Function2)ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            Function2 function2 = ComposeUiNode.Companion.getSetCompositeKeyHash();
            boolean bl6 = false;
            Composer composer3 = composer2;
            boolean bl7 = false;
            if (composer3.getInserting() || !Intrinsics.areEqual((Object)composer3.rememberedValue(), (Object)n3)) {
                composer3.updateRememberedValue((Object)n3);
                composer2.apply((Object)n3, function2);
            }
            Updater.set-impl((Composer)composer2, (Object)modifier2, (Function2)ComposeUiNode.Companion.getSetModifier());
            int n5 = 0xE & $changed\3 >> 6;
            void $composer\7 = $composer\1;
            boolean bl8 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\7, (int)1833054614, (String)"C72@3469L9:Box.kt#2w3rfo");
            int n6 = 6 | 0x70 & $changed\1 >> 6;
            void var32_32 = $composer\7;
            BoxScope boxScope = (BoxScope)BoxScopeInstance.INSTANCE;
            boolean bl9 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\8, (int)1711367532, (String)"C2141@99033L16:GroupActivity.kt#83vr7l");
            if (StringsKt.isBlank((CharSequence)GroupActivity.GroupEditAlbumSheet$lambda$341((MutableState<TextFieldValue>)$albumNameField$delegate).getText())) {
                $composer\8.startReplaceGroup(1711399523);
                ComposerKt.sourceInformation((Composer)$composer\8, (String)"2135@98714L244");
                TextKt.Text--4IGK_g((String)"\u8bf7\u8f93\u5165\u5206\u7ec4\u540d\u79f0", null, (long)$palette.getTextTertiary-0d7_KjU(), (long)TextUnitKt.getSp((int)15), null, null, null, (long)0L, null, null, (long)0L, (int)0, (boolean)false, (int)0, (int)0, null, null, (Composer)$composer\8, (int)3078, (int)0, (int)131058);
                $composer\8.endReplaceGroup();
            } else {
                $composer\8.startReplaceGroup(1613505863);
                $composer\8.endReplaceGroup();
            }
            innerTextField.invoke((Object)$composer\8, (Object)(0xE & $dirty));
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\8);
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\7);
            $composer\1.endNode();
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\1);
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\1);
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\1);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        } else {
            $composer.skipToGroupEnd();
        }
        return Unit.INSTANCE;
    }

    /*
     * WARNING - void declaration
     */
    @Composable
    @ComposableTarget(applier="androidx.compose.ui.UiComposable")
    private static final Unit GroupEditAlbumSheet$lambda$385$lambda$384$lambda$383$lambda$382$lambda$381$lambda$375(NoMemoPalette $palette, Function1 $onNameChange, MutableState $albumNameField$delegate, ColumnScope $this$Card, Composer $composer, int $changed) {
        Intrinsics.checkNotNullParameter((Object)$this$Card, (String)"$this$Card");
        ComposerKt.sourceInformation((Composer)$composer, (String)"C2115@97623L170,2129@98360L753,2113@97503L1610:GroupActivity.kt#83vr7l");
        if ($composer.shouldExecute(($changed & 0x11) != 16, $changed & 1)) {
            Object object;
            void $this$cache\3;
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart((int)1697223000, (int)$changed, (int)-1, (String)"com.han.nomemo.GroupActivity.GroupEditAlbumSheet.<anonymous>.<anonymous>.<anonymous>.<anonymous>.<anonymous>.<anonymous> (GroupActivity.kt:2113)");
            }
            TextFieldValue textFieldValue = GroupActivity.GroupEditAlbumSheet$lambda$341((MutableState<TextFieldValue>)$albumNameField$delegate);
            TextStyle textStyle = new TextStyle($palette.getTextPrimary-0d7_KjU(), TextUnitKt.getSp((int)16), null, null, null, null, null, 0L, null, null, null, 0L, null, null, null, 0, 0, TextUnitKt.getSp((int)24), null, null, null, 0, 0, null, 0xFDFFFC, null);
            int n = 56;
            boolean bl = false;
            int n2 = 14;
            boolean $i$f$getDp\2\21302 = false;
            Modifier modifier = PaddingKt.padding-VpY3zN4$default((Modifier)SizeKt.height-3ABfNKs((Modifier)SizeKt.fillMaxWidth$default((Modifier)((Modifier)Modifier.Companion), (float)0.0f, (int)1, null), (float)Dp.constructor-impl((float)n)), (float)Dp.constructor-impl((float)n2), (float)0.0f, (int)2, null);
            TextFieldValue textFieldValue2 = textFieldValue;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)-1056264510, (String)"CC(remember):GroupActivity.kt#9igjgp");
            Composer $i$f$getDp\2\21302 = $composer;
            boolean bl2 = $composer.changed((Object)$onNameChange);
            boolean bl3 = false;
            Object object2 = $this$cache\3.rememberedValue();
            boolean bl4 = false;
            if (bl2 || object2 == Composer.Companion.getEmpty()) {
                TextFieldValue textFieldValue3 = textFieldValue2;
                boolean bl5 = false;
                textFieldValue2 = textFieldValue3;
                Function1 function1 = arg_0 -> GroupActivity.GroupEditAlbumSheet$lambda$385$lambda$384$lambda$383$lambda$382$lambda$381$lambda$375$lambda$372$lambda$371($onNameChange, $albumNameField$delegate, arg_0);
                $this$cache\3.updateRememberedValue((Object)function1);
                object = function1;
            } else {
                object = object2;
            }
            Function1 function1 = (Function1)object;
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
            BasicTextFieldKt.BasicTextField((TextFieldValue)textFieldValue2, (Function1)function1, (Modifier)modifier, (boolean)false, (boolean)false, (TextStyle)textStyle, null, null, (boolean)true, (int)0, (int)0, null, null, null, null, (Function3)((Function3)ComposableLambdaKt.rememberComposableLambda((int)1515098581, (boolean)true, (arg_0, arg_1, arg_2) -> GroupActivity.GroupEditAlbumSheet$lambda$385$lambda$384$lambda$383$lambda$382$lambda$381$lambda$375$lambda$374($palette, $albumNameField$delegate, arg_0, arg_1, arg_2), (Composer)$composer, (int)54)), (Composer)$composer, (int)100663680, (int)196608, (int)32472);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        } else {
            $composer.skipToGroupEnd();
        }
        return Unit.INSTANCE;
    }

    private static final Unit GroupEditAlbumSheet$lambda$385$lambda$384$lambda$383$lambda$382$lambda$381$lambda$380$lambda$377$lambda$376(Function1 $onDescriptionChange, MutableState $albumDescriptionField$delegate, TextFieldValue updated) {
        Intrinsics.checkNotNullParameter((Object)updated, (String)"updated");
        GroupActivity.GroupEditAlbumSheet$lambda$345((MutableState<TextFieldValue>)$albumDescriptionField$delegate, updated);
        $onDescriptionChange.invoke((Object)updated.getText());
        return Unit.INSTANCE;
    }

    /*
     * WARNING - void declaration
     */
    @Composable
    @ComposableInferredTarget(scheme="[androidx.compose.ui.UiComposable[androidx.compose.ui.UiComposable]]")
    private static final Unit GroupEditAlbumSheet$lambda$385$lambda$384$lambda$383$lambda$382$lambda$381$lambda$380$lambda$379(ScrollState $descriptionScrollState, NoMemoPalette $palette, MutableState $albumDescriptionField$delegate, Function2 innerTextField, Composer $composer, int $changed) {
        Intrinsics.checkNotNullParameter((Object)innerTextField, (String)"innerTextField");
        ComposerKt.sourceInformation((Composer)$composer, (String)"CN(innerTextField)2174@100697L873:GroupActivity.kt#83vr7l");
        int $dirty = $changed;
        if (($changed & 6) == 0) {
            $dirty |= $composer.changedInstance((Object)innerTextField) ? 4 : 2;
        }
        if ($composer.shouldExecute(($dirty & 0x13) != 18, $dirty & 1)) {
            void $composer\8;
            void $changed\3;
            void $changed\2;
            void modifier\2;
            void $changed\1;
            void modifier\1;
            void contentAlignment\1;
            void $composer\1;
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart((int)205986700, (int)$dirty, (int)-1, (String)"com.han.nomemo.GroupActivity.GroupEditAlbumSheet.<anonymous>.<anonymous>.<anonymous>.<anonymous>.<anonymous>.<anonymous>.<anonymous> (GroupActivity.kt:2174)");
            }
            Modifier modifier = ScrollKt.verticalScroll$default((Modifier)SizeKt.fillMaxSize$default((Modifier)((Modifier)Modifier.Companion), (float)0.0f, (int)1, null), (ScrollState)$descriptionScrollState, (boolean)false, null, (boolean)false, (int)14, null);
            Alignment alignment = Alignment.Companion.getTopStart();
            Composer composer = $composer;
            int n = 48;
            boolean bl = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\1, (int)1042775818, (String)"CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo");
            boolean bl2 = false;
            MeasurePolicy measurePolicy = BoxKt.maybeCachedBoxMeasurePolicy((Alignment)contentAlignment\1, (boolean)bl2);
            void var14_14 = modifier\1;
            int n2 = 0x70 & $changed\1 << 3;
            boolean bl3 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\1, (int)-1159599143, (String)"CC(Layout)P(!1,2)80@3267L27,83@3433L360:Layout.kt#80mrfh");
            int n3 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode((Composer)$composer\1, (int)0));
            CompositionLocalMap compositionLocalMap = $composer\1.getCurrentCompositionLocalMap();
            Modifier modifier2 = ComposedModifierKt.materializeModifier((Composer)$composer\1, (Modifier)modifier\2);
            Function0 function0 = ComposeUiNode.Companion.getConstructor();
            int n4 = 6 | 0x380 & $changed\2 << 6;
            boolean bl4 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\1, (int)-553112988, (String)"CC(ReusableComposeNode)N(factory,update,content)399@15590L9:Composables.kt#9igjgp");
            if (!($composer\1.getApplier() instanceof Applier)) {
                ComposablesKt.invalidApplier();
            }
            $composer\1.startReusableNode();
            if ($composer\1.getInserting()) {
                void factory\3;
                $composer\1.createNode((Function0)factory\3);
            } else {
                $composer\1.useNode();
            }
            Composer composer2 = Updater.constructor-impl((Composer)$composer\1);
            boolean bl5 = false;
            Updater.set-impl((Composer)composer2, (Object)measurePolicy, (Function2)ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.set-impl((Composer)composer2, (Object)compositionLocalMap, (Function2)ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            Function2 function2 = ComposeUiNode.Companion.getSetCompositeKeyHash();
            boolean bl6 = false;
            Composer composer3 = composer2;
            boolean bl7 = false;
            if (composer3.getInserting() || !Intrinsics.areEqual((Object)composer3.rememberedValue(), (Object)n3)) {
                composer3.updateRememberedValue((Object)n3);
                composer2.apply((Object)n3, function2);
            }
            Updater.set-impl((Composer)composer2, (Object)modifier2, (Function2)ComposeUiNode.Companion.getSetModifier());
            int n5 = 0xE & $changed\3 >> 6;
            void $composer\7 = $composer\1;
            boolean bl8 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\7, (int)1833054614, (String)"C72@3469L9:Box.kt#2w3rfo");
            int n6 = 6 | 0x70 & $changed\1 >> 6;
            void var33_33 = $composer\7;
            BoxScope boxScope = (BoxScope)BoxScopeInstance.INSTANCE;
            boolean bl9 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\8, (int)-291713023, (String)"C2188@101520L16:GroupActivity.kt#83vr7l");
            if (StringsKt.isBlank((CharSequence)GroupActivity.GroupEditAlbumSheet$lambda$344((MutableState<TextFieldValue>)$albumDescriptionField$delegate).getText())) {
                $composer\8.startReplaceGroup(-291674305);
                ComposerKt.sourceInformation((Composer)$composer\8, (String)"2181@101124L321");
                TextKt.Text--4IGK_g((String)"\u8bf7\u8f93\u5165\u5206\u7ec4\u63cf\u8ff0\uff0c\u6216\u671f\u671bAI\u5e2e\u4f60\u5206\u7ec4\u7684\u63cf\u8ff0", null, (long)$palette.getTextTertiary-0d7_KjU(), (long)TextUnitKt.getSp((int)15), null, null, null, (long)0L, null, null, (long)TextUnitKt.getSp((int)22), (int)0, (boolean)false, (int)0, (int)0, null, null, (Composer)$composer\8, (int)3078, (int)6, (int)130034);
                $composer\8.endReplaceGroup();
            } else {
                $composer\8.startReplaceGroup(-391961072);
                $composer\8.endReplaceGroup();
            }
            innerTextField.invoke((Object)$composer\8, (Object)(0xE & $dirty));
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\8);
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\7);
            $composer\1.endNode();
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\1);
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\1);
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\1);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        } else {
            $composer.skipToGroupEnd();
        }
        return Unit.INSTANCE;
    }

    /*
     * WARNING - void declaration
     */
    @Composable
    @ComposableTarget(applier="androidx.compose.ui.UiComposable")
    private static final Unit GroupEditAlbumSheet$lambda$385$lambda$384$lambda$383$lambda$382$lambda$381$lambda$380(NoMemoPalette $palette, Function1 $onDescriptionChange, MutableState $albumDescriptionField$delegate, ScrollState $descriptionScrollState, ColumnScope $this$Card, Composer $composer, int $changed) {
        Intrinsics.checkNotNullParameter((Object)$this$Card, (String)"$this$Card");
        ComposerKt.sourceInformation((Composer)$composer, (String)"C2160@99926L184,2173@100645L955,2158@99799L1801:GroupActivity.kt#83vr7l");
        if ($composer.shouldExecute(($changed & 0x11) != 16, $changed & 1)) {
            Object object;
            void $this$cache\4;
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart((int)-866105777, (int)$changed, (int)-1, (String)"com.han.nomemo.GroupActivity.GroupEditAlbumSheet.<anonymous>.<anonymous>.<anonymous>.<anonymous>.<anonymous>.<anonymous> (GroupActivity.kt:2158)");
            }
            TextFieldValue textFieldValue = GroupActivity.GroupEditAlbumSheet$lambda$344((MutableState<TextFieldValue>)$albumDescriptionField$delegate);
            TextStyle textStyle = new TextStyle($palette.getTextPrimary-0d7_KjU(), TextUnitKt.getSp((int)16), null, null, null, null, null, 0L, null, null, null, 0L, null, null, null, 0, 0, TextUnitKt.getSp((int)24), null, null, null, 0, 0, null, 0xFDFFFC, null);
            int n = 148;
            boolean bl = false;
            int n2 = 14;
            boolean bl2 = false;
            int n3 = 14;
            boolean $i$f$getDp\3\21742 = false;
            Modifier modifier = PaddingKt.padding-VpY3zN4((Modifier)SizeKt.height-3ABfNKs((Modifier)SizeKt.fillMaxWidth$default((Modifier)((Modifier)Modifier.Companion), (float)0.0f, (int)1, null), (float)Dp.constructor-impl((float)n)), (float)Dp.constructor-impl((float)n2), (float)Dp.constructor-impl((float)n3));
            TextFieldValue textFieldValue2 = textFieldValue;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer, (int)896008935, (String)"CC(remember):GroupActivity.kt#9igjgp");
            Composer $i$f$getDp\3\21742 = $composer;
            boolean bl3 = $composer.changed((Object)$onDescriptionChange);
            boolean bl4 = false;
            Object object2 = $this$cache\4.rememberedValue();
            boolean bl5 = false;
            if (bl3 || object2 == Composer.Companion.getEmpty()) {
                TextFieldValue textFieldValue3 = textFieldValue2;
                boolean bl6 = false;
                textFieldValue2 = textFieldValue3;
                Function1 function1 = arg_0 -> GroupActivity.GroupEditAlbumSheet$lambda$385$lambda$384$lambda$383$lambda$382$lambda$381$lambda$380$lambda$377$lambda$376($onDescriptionChange, $albumDescriptionField$delegate, arg_0);
                $this$cache\4.updateRememberedValue((Object)function1);
                object = function1;
            } else {
                object = object2;
            }
            Function1 function1 = (Function1)object;
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer);
            BasicTextFieldKt.BasicTextField((TextFieldValue)textFieldValue2, (Function1)function1, (Modifier)modifier, (boolean)false, (boolean)false, (TextStyle)textStyle, null, null, (boolean)false, (int)0, (int)0, null, null, null, null, (Function3)((Function3)ComposableLambdaKt.rememberComposableLambda((int)205986700, (boolean)true, (arg_0, arg_1, arg_2) -> GroupActivity.GroupEditAlbumSheet$lambda$385$lambda$384$lambda$383$lambda$382$lambda$381$lambda$380$lambda$379($descriptionScrollState, $palette, $albumDescriptionField$delegate, arg_0, arg_1, arg_2), (Composer)$composer, (int)54)), (Composer)$composer, (int)384, (int)196608, (int)32728);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        } else {
            $composer.skipToGroupEnd();
        }
        return Unit.INSTANCE;
    }

    /*
     * WARNING - void declaration
     */
    @Composable
    @ComposableTarget(applier="androidx.compose.ui.UiComposable")
    private static final Unit GroupEditAlbumSheet$lambda$385$lambda$384$lambda$383(float $bodyHeight, long $dragHandleColor, NoMemoSheetDragController $sheetDrag, NoMemoAdaptiveSpec $adaptive, Function0 $requestConfirm, Function0 $tryDismiss, String $title, NoMemoPalette $palette, long $inputSurface, boolean $showOrganizeToggle, boolean $autoClassifyEnabled, Function1 $onAutoClassifyEnabledChange, Function1 $onNameChange, MutableState $albumNameField$delegate, Function1 $onDescriptionChange, MutableState $albumDescriptionField$delegate, ScrollState $descriptionScrollState, ColumnScope $this$Card, Composer $composer, int $changed) {
        Intrinsics.checkNotNullParameter((Object)$this$Card, (String)"$this$Card");
        ComposerKt.sourceInformation((Composer)$composer, (String)"C2048@94455L7684:GroupActivity.kt#83vr7l");
        if ($composer.shouldExecute(($changed & 0x11) != 16, $changed & 1)) {
            void other\52;
            void arg0\52;
            void $composer\42;
            void $changed\37;
            void $changed\36;
            void modifier\36;
            void modifier\35;
            void $changed\35;
            void $composer\35;
            void $composer\34;
            void $changed\29;
            void $changed\28;
            void modifier\28;
            void modifier\27;
            void $changed\27;
            void horizontalAlignment\27;
            void $composer\27;
            Object object;
            Function0 function0;
            void $this$cache\23;
            void $composer\22;
            int n;
            Function0 function02;
            int n2;
            void modifier\16;
            Modifier modifier;
            boolean bl;
            void verticalAlignment\15;
            void $composer\15;
            void $composer\12;
            void $changed\7;
            void $changed\6;
            void modifier\6;
            void modifier\5;
            void $changed\5;
            void $composer\5;
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart((int)1499512954, (int)$changed, (int)-1, (String)"com.han.nomemo.GroupActivity.GroupEditAlbumSheet.<anonymous>.<anonymous>.<anonymous> (GroupActivity.kt:2048)");
            }
            int n3 = 14;
            boolean bl2 = false;
            int n4 = 10;
            boolean bl3 = false;
            int n5 = 14;
            boolean bl4 = false;
            boolean $this$dp\52 = false;
            boolean bl5 = false;
            Modifier $this$dp\52 = PaddingKt.padding-qDBjuR0((Modifier)SizeKt.heightIn-VpY3zN4$default((Modifier)SizeKt.fillMaxWidth$default((Modifier)((Modifier)Modifier.Companion), (float)0.0f, (int)1, null), (float)0.0f, (float)$bodyHeight, (int)1, null), (float)Dp.constructor-impl((float)n3), (float)Dp.constructor-impl((float)n4), (float)Dp.constructor-impl((float)n5), (float)Dp.constructor-impl((float)((float)$this$dp\52)));
            Composer composer = $composer;
            boolean bl6 = false;
            boolean bl7 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\5, (int)1341605231, (String)"CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo");
            Arrangement.Vertical vertical = Arrangement.INSTANCE.getTop();
            Alignment.Horizontal horizontal = Alignment.Companion.getStart();
            MeasurePolicy measurePolicy = ColumnKt.columnMeasurePolicy((Arrangement.Vertical)vertical, (Alignment.Horizontal)horizontal, (Composer)$composer\5, (int)(0xE & $changed\5 >> 3 | 0x70 & $changed\5 >> 3));
            void var29_29 = modifier\5;
            int n6 = 0x70 & $changed\5 << 3;
            boolean bl8 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\5, (int)-1159599143, (String)"CC(Layout)P(!1,2)80@3267L27,83@3433L360:Layout.kt#80mrfh");
            int n7 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode((Composer)$composer\5, (int)0));
            CompositionLocalMap compositionLocalMap = $composer\5.getCurrentCompositionLocalMap();
            Modifier modifier2 = ComposedModifierKt.materializeModifier((Composer)$composer\5, (Modifier)modifier\6);
            Function0 function03 = ComposeUiNode.Companion.getConstructor();
            int n8 = 6 | 0x380 & $changed\6 << 6;
            boolean bl9 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\5, (int)-553112988, (String)"CC(ReusableComposeNode)N(factory,update,content)399@15590L9:Composables.kt#9igjgp");
            if (!($composer\5.getApplier() instanceof Applier)) {
                ComposablesKt.invalidApplier();
            }
            $composer\5.startReusableNode();
            if ($composer\5.getInserting()) {
                void factory\7;
                $composer\5.createNode((Function0)factory\7);
            } else {
                $composer\5.useNode();
            }
            Composer composer2 = Updater.constructor-impl((Composer)$composer\5);
            boolean bl10 = false;
            Updater.set-impl((Composer)composer2, (Object)measurePolicy, (Function2)ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.set-impl((Composer)composer2, (Object)compositionLocalMap, (Function2)ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            Function2 function2 = ComposeUiNode.Companion.getSetCompositeKeyHash();
            boolean bl11 = false;
            Composer composer3 = composer2;
            boolean bl12 = false;
            if (composer3.getInserting() || !Intrinsics.areEqual((Object)composer3.rememberedValue(), (Object)n7)) {
                composer3.updateRememberedValue((Object)n7);
                composer2.apply((Object)n7, function2);
            }
            Updater.set-impl((Composer)composer2, (Object)modifier2, (Function2)ComposeUiNode.Companion.getSetModifier());
            int n9 = 0xE & $changed\7 >> 6;
            void $composer\11 = $composer\5;
            boolean bl13 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\11, (int)2093002350, (String)"C89@4557L9:Column.kt#2w3rfo");
            int n10 = 6 | 0x70 & $changed\5 >> 6;
            void var48_48 = $composer\11;
            ColumnScope columnScope = (ColumnScope)ColumnScopeInstance.INSTANCE;
            boolean bl14 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\12, (int)408492942, (String)"C2054@94724L221,2060@94967L1606,2094@96595L5526:GroupActivity.kt#83vr7l");
            ComposeUiKt.NoMemoSheetDragHandle-KTwxG1Y($dragHandleColor, $sheetDrag, columnScope.align((Modifier)Modifier.Companion, Alignment.Companion.getCenterHorizontally()), (Composer)$composer\12, 0, 0);
            int n11 = 12;
            boolean bl15 = false;
            int $this$dp\152 = 12;
            boolean bl16 = false;
            Modifier $this$dp\152 = PaddingKt.padding-qDBjuR0$default((Modifier)SizeKt.fillMaxWidth$default((Modifier)((Modifier)Modifier.Companion), (float)0.0f, (int)1, null), (float)0.0f, (float)Dp.constructor-impl((float)n11), (float)0.0f, (float)Dp.constructor-impl((float)$this$dp\152), (int)5, null);
            Alignment.Vertical vertical2 = Alignment.Companion.getCenterVertically();
            void var54_56 = $composer\12;
            int n12 = 390;
            boolean bl17 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\15, (int)844473419, (String)"CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo");
            Arrangement.Horizontal horizontal2 = Arrangement.INSTANCE.getStart();
            MeasurePolicy measurePolicy2 = RowKt.rowMeasurePolicy((Arrangement.Horizontal)horizontal2, (Alignment.Vertical)verticalAlignment\15, (Composer)$composer\15, (int)(0xE & bl >> 3 | 0x70 & bl >> 3));
            void var58_60 = modifier;
            int n13 = 0x70 & bl << 3;
            boolean bl18 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\15, (int)-1159599143, (String)"CC(Layout)P(!1,2)80@3267L27,83@3433L360:Layout.kt#80mrfh");
            int n14 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode((Composer)$composer\15, (int)0));
            CompositionLocalMap compositionLocalMap2 = $composer\15.getCurrentCompositionLocalMap();
            Modifier modifier3 = ComposedModifierKt.materializeModifier((Composer)$composer\15, (Modifier)modifier\16);
            Function0 function04 = ComposeUiNode.Companion.getConstructor();
            int n15 = 6 | 0x380 & n2 << 6;
            boolean bl19 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\15, (int)-553112988, (String)"CC(ReusableComposeNode)N(factory,update,content)399@15590L9:Composables.kt#9igjgp");
            if (!($composer\15.getApplier() instanceof Applier)) {
                ComposablesKt.invalidApplier();
            }
            $composer\15.startReusableNode();
            if ($composer\15.getInserting()) {
                $composer\15.createNode(function02);
            } else {
                $composer\15.useNode();
            }
            Composer composer4 = Updater.constructor-impl((Composer)$composer\15);
            boolean bl20 = false;
            Updater.set-impl((Composer)composer4, (Object)measurePolicy2, (Function2)ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.set-impl((Composer)composer4, (Object)compositionLocalMap2, (Function2)ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            Function2 function22 = ComposeUiNode.Companion.getSetCompositeKeyHash();
            boolean bl21 = false;
            Composer composer5 = composer4;
            boolean bl22 = false;
            if (composer5.getInserting() || !Intrinsics.areEqual((Object)composer5.rememberedValue(), (Object)n14)) {
                composer5.updateRememberedValue((Object)n14);
                composer4.apply((Object)n14, function22);
            }
            Updater.set-impl((Composer)composer4, (Object)modifier3, (Function2)ComposeUiNode.Companion.getSetModifier());
            int n16 = 0xE & n >> 6;
            void $composer\21 = $composer\15;
            boolean bl23 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\21, (int)1456264949, (String)"C101@5233L9:Row.kt#2w3rfo");
            int n17 = 6 | 0x70 & bl >> 6;
            void var77_79 = $composer\21;
            RowScope rowScope = (RowScope)RowScopeInstance.INSTANCE;
            boolean bl24 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\22, (int)384532342, (String)"C2068@95383L31,2069@95454L16,2066@95246L315,2072@95586L626,2088@96374L32,2086@96237L314:GroupActivity.kt#83vr7l");
            int n18 = R.drawable.ic_sheet_close;
            String string2 = StringResources_androidKt.stringResource((int)R.string.cancel, (Composer)$composer\22, (int)0);
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\22, (int)12409636, (String)"CC(remember):GroupActivity.kt#9igjgp");
            void var80_82 = $composer\22;
            boolean invalid\242 = false;
            boolean $i$f$cache\23\20712 = false;
            Object it\242 = $this$cache\23.rememberedValue();
            boolean bl25 = false;
            if (it\242 == Composer.Companion.getEmpty()) {
                String string3 = string2;
                int n19 = n18;
                boolean bl26 = false;
                Function0 function05 = () -> GroupActivity.GroupEditAlbumSheet$lambda$385$lambda$384$lambda$383$lambda$382$lambda$370$lambda$368$lambda$367($tryDismiss);
                n18 = n19;
                string2 = string3;
                function0 = function05;
                $this$cache\23.updateRememberedValue((Object)function0);
                object = function0;
            } else {
                object = it\242;
            }
            Function0 function06 = (Function0)object;
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\22);
            ComposeUiKt.GlassIconCircleButton-WH-ejsw(n18, string2, (Function0<Unit>)function06, null, $adaptive.getTopActionButtonSize-D9Ej5fM(), null, (Composer)$composer\22, 384, 40);
            int $this$dp\272 = 10;
            boolean bl27 = false;
            Modifier $this$dp\272 = PaddingKt.padding-VpY3zN4$default((Modifier)RowScope.weight$default((RowScope)rowScope, (Modifier)((Modifier)Modifier.Companion), (float)1.0f, (boolean)false, (int)2, null), (float)Dp.constructor-impl((float)$this$dp\272), (float)0.0f, (int)2, null);
            Alignment.Horizontal invalid\242 = Alignment.Companion.getCenterHorizontally();
            void $i$f$cache\23\20712 = $composer\22;
            int it\242 = 384;
            boolean bl28 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\27, (int)1341605231, (String)"CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo");
            Arrangement.Vertical vertical3 = Arrangement.INSTANCE.getTop();
            MeasurePolicy measurePolicy3 = ColumnKt.columnMeasurePolicy((Arrangement.Vertical)vertical3, (Alignment.Horizontal)horizontalAlignment\27, (Composer)$composer\27, (int)(0xE & $changed\27 >> 3 | 0x70 & $changed\27 >> 3));
            function0 = modifier\27;
            int n20 = 0x70 & $changed\27 << 3;
            boolean bl29 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\27, (int)-1159599143, (String)"CC(Layout)P(!1,2)80@3267L27,83@3433L360:Layout.kt#80mrfh");
            int n21 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode((Composer)$composer\27, (int)0));
            CompositionLocalMap compositionLocalMap3 = $composer\27.getCurrentCompositionLocalMap();
            Modifier modifier4 = ComposedModifierKt.materializeModifier((Composer)$composer\27, (Modifier)modifier\28);
            Function0 function07 = ComposeUiNode.Companion.getConstructor();
            int n22 = 6 | 0x380 & $changed\28 << 6;
            boolean bl30 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\27, (int)-553112988, (String)"CC(ReusableComposeNode)N(factory,update,content)399@15590L9:Composables.kt#9igjgp");
            if (!($composer\27.getApplier() instanceof Applier)) {
                ComposablesKt.invalidApplier();
            }
            $composer\27.startReusableNode();
            if ($composer\27.getInserting()) {
                void factory\29;
                $composer\27.createNode((Function0)factory\29);
            } else {
                $composer\27.useNode();
            }
            Composer composer6 = Updater.constructor-impl((Composer)$composer\27);
            boolean bl31 = false;
            Updater.set-impl((Composer)composer6, (Object)measurePolicy3, (Function2)ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.set-impl((Composer)composer6, (Object)compositionLocalMap3, (Function2)ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            Function2 function23 = ComposeUiNode.Companion.getSetCompositeKeyHash();
            boolean bl32 = false;
            Composer composer7 = composer6;
            boolean bl33 = false;
            if (composer7.getInserting() || !Intrinsics.areEqual((Object)composer7.rememberedValue(), (Object)n21)) {
                composer7.updateRememberedValue((Object)n21);
                composer6.apply((Object)n21, function23);
            }
            Updater.set-impl((Composer)composer6, (Object)modifier4, (Function2)ComposeUiNode.Companion.getSetModifier());
            int n23 = 0xE & $changed\29 >> 6;
            void $composer\33 = $composer\27;
            boolean bl34 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\33, (int)2093002350, (String)"C89@4557L9:Column.kt#2w3rfo");
            int n24 = 6 | 0x70 & $changed\27 >> 6;
            void var109_126 = $composer\33;
            ColumnScope columnScope2 = (ColumnScope)ColumnScopeInstance.INSTANCE;
            boolean bl35 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\34, (int)-888568726, (String)"C2078@95883L303:GroupActivity.kt#83vr7l");
            TextKt.Text--4IGK_g((String)$title, null, (long)$palette.getTextPrimary-0d7_KjU(), (long)TextUnitKt.getSp((int)18), null, (FontWeight)FontWeight.Companion.getSemiBold(), null, (long)0L, null, null, (long)0L, (int)0, (boolean)false, (int)1, (int)0, null, null, (Composer)$composer\34, (int)199680, (int)3072, (int)122834);
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\34);
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\33);
            $composer\27.endNode();
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\27);
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\27);
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\27);
            ComposeUiKt.GlassIconCircleButton-WH-ejsw(R.drawable.ic_sheet_check, StringResources_androidKt.stringResource((int)R.string.confirm, (Composer)$composer\22, (int)0), (Function0<Unit>)$requestConfirm, null, $adaptive.getTopActionButtonSize-D9Ej5fM(), null, (Composer)$composer\22, 0, 40);
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\22);
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\21);
            $composer\15.endNode();
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\15);
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\15);
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\15);
            modifier = ColumnScope.weight$default((ColumnScope)columnScope, (Modifier)SizeKt.fillMaxWidth$default((Modifier)((Modifier)Modifier.Companion), (float)0.0f, (int)1, null), (float)1.0f, (boolean)false, (int)2, null);
            $composer\15 = $composer\12;
            bl = false;
            boolean bl36 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\35, (int)1341605231, (String)"CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo");
            Arrangement.Vertical vertical4 = Arrangement.INSTANCE.getTop();
            Alignment.Horizontal horizontal3 = Alignment.Companion.getStart();
            MeasurePolicy measurePolicy4 = ColumnKt.columnMeasurePolicy((Arrangement.Vertical)vertical4, (Alignment.Horizontal)horizontal3, (Composer)$composer\35, (int)(0xE & $changed\35 >> 3 | 0x70 & $changed\35 >> 3));
            modifier\16 = modifier\35;
            n2 = 0x70 & $changed\35 << 3;
            boolean bl37 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\35, (int)-1159599143, (String)"CC(Layout)P(!1,2)80@3267L27,83@3433L360:Layout.kt#80mrfh");
            int n25 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode((Composer)$composer\35, (int)0));
            CompositionLocalMap compositionLocalMap4 = $composer\35.getCurrentCompositionLocalMap();
            Modifier modifier5 = ComposedModifierKt.materializeModifier((Composer)$composer\35, (Modifier)modifier\36);
            function02 = ComposeUiNode.Companion.getConstructor();
            n = 6 | 0x380 & $changed\36 << 6;
            boolean bl38 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\35, (int)-553112988, (String)"CC(ReusableComposeNode)N(factory,update,content)399@15590L9:Composables.kt#9igjgp");
            if (!($composer\35.getApplier() instanceof Applier)) {
                ComposablesKt.invalidApplier();
            }
            $composer\35.startReusableNode();
            if ($composer\35.getInserting()) {
                void factory\37;
                $composer\35.createNode((Function0)factory\37);
            } else {
                $composer\35.useNode();
            }
            Composer composer8 = Updater.constructor-impl((Composer)$composer\35);
            boolean bl39 = false;
            Updater.set-impl((Composer)composer8, (Object)measurePolicy4, (Function2)ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.set-impl((Composer)composer8, (Object)compositionLocalMap4, (Function2)ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            Function2 function24 = ComposeUiNode.Companion.getSetCompositeKeyHash();
            boolean bl40 = false;
            Composer composer9 = composer8;
            boolean bl41 = false;
            if (composer9.getInserting() || !Intrinsics.areEqual((Object)composer9.rememberedValue(), (Object)n25)) {
                composer9.updateRememberedValue((Object)n25);
                composer8.apply((Object)n25, function24);
            }
            Updater.set-impl((Composer)composer8, (Object)modifier5, (Function2)ComposeUiNode.Companion.getSetModifier());
            int n26 = 0xE & $changed\37 >> 6;
            void $composer\41 = $composer\35;
            boolean bl42 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\41, (int)2093002350, (String)"C89@4557L9:Column.kt#2w3rfo");
            int n27 = 6 | 0x70 & $changed\35 >> 6;
            $composer\22 = $composer\41;
            ColumnScope columnScope3 = (ColumnScope)ColumnScopeInstance.INSTANCE;
            boolean bl43 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\42, (int)-1324380203, (String)"C2099@96779L326,2111@97405L41,2112@97473L1666,2106@97130L2009,2146@99165L326,2156@99701L41,2157@99769L1857,2153@99516L2110,2201@102030L69:GroupActivity.kt#83vr7l");
            long l = $palette.getTextSecondary-0d7_KjU();
            long l2 = TextUnitKt.getSp((int)15);
            FontWeight fontWeight = FontWeight.Companion.getSemiBold();
            int n28 = 2;
            boolean bl44 = false;
            int n29 = 6;
            boolean bl45 = false;
            Modifier modifier6 = PaddingKt.padding-qDBjuR0$default((Modifier)((Modifier)Modifier.Companion), (float)Dp.constructor-impl((float)n28), (float)0.0f, (float)0.0f, (float)Dp.constructor-impl((float)n29), (int)6, null);
            TextKt.Text--4IGK_g((String)"\u5206\u7ec4\u540d\u79f0", (Modifier)modifier6, (long)l, (long)l2, null, (FontWeight)fontWeight, null, (long)0L, null, null, (long)0L, (int)0, (boolean)false, (int)0, (int)0, null, null, (Composer)$composer\42, (int)199734, (int)0, (int)131024);
            int n30 = 12;
            boolean bl46 = false;
            int n31 = 22;
            boolean bl47 = false;
            CardKt.Card((Modifier)PaddingKt.padding-qDBjuR0$default((Modifier)SizeKt.fillMaxWidth$default((Modifier)((Modifier)Modifier.Companion), (float)0.0f, (int)1, null), (float)0.0f, (float)0.0f, (float)0.0f, (float)Dp.constructor-impl((float)n30), (int)7, null), (Shape)((Shape)ComposeUiKt.noMemoG2RoundedShape-0680j_4(Dp.constructor-impl((float)n31))), (CardColors)CardDefaults.INSTANCE.cardColors-ro_MJ88($inputSurface, 0L, 0L, 0L, (Composer)$composer\42, CardDefaults.$stable << 12, 14), null, null, (Function3)((Function3)ComposableLambdaKt.rememberComposableLambda((int)1697223000, (boolean)true, (arg_0, arg_1, arg_2) -> GroupActivity.GroupEditAlbumSheet$lambda$385$lambda$384$lambda$383$lambda$382$lambda$381$lambda$375($palette, $onNameChange, $albumNameField$delegate, arg_0, arg_1, arg_2), (Composer)$composer\42, (int)54)), (Composer)$composer\42, (int)196614, (int)24);
            l = $palette.getTextSecondary-0d7_KjU();
            l2 = TextUnitKt.getSp((int)15);
            fontWeight = FontWeight.Companion.getSemiBold();
            int n32 = 2;
            boolean bl48 = false;
            int n33 = 6;
            boolean bl49 = false;
            modifier6 = PaddingKt.padding-qDBjuR0$default((Modifier)((Modifier)Modifier.Companion), (float)Dp.constructor-impl((float)n32), (float)0.0f, (float)0.0f, (float)Dp.constructor-impl((float)n33), (int)6, null);
            TextKt.Text--4IGK_g((String)"\u5206\u7ec4\u63cf\u8ff0", (Modifier)modifier6, (long)l, (long)l2, null, (FontWeight)fontWeight, null, (long)0L, null, null, (long)0L, (int)0, (boolean)false, (int)0, (int)0, null, null, (Composer)$composer\42, (int)199734, (int)0, (int)131024);
            int n34 = 22;
            boolean bl50 = false;
            CardKt.Card((Modifier)SizeKt.fillMaxWidth$default((Modifier)((Modifier)Modifier.Companion), (float)0.0f, (int)1, null), (Shape)((Shape)ComposeUiKt.noMemoG2RoundedShape-0680j_4(Dp.constructor-impl((float)n34))), (CardColors)CardDefaults.INSTANCE.cardColors-ro_MJ88($inputSurface, 0L, 0L, 0L, (Composer)$composer\42, CardDefaults.$stable << 12, 14), null, null, (Function3)((Function3)ComposableLambdaKt.rememberComposableLambda((int)-866105777, (boolean)true, (arg_0, arg_1, arg_2) -> GroupActivity.GroupEditAlbumSheet$lambda$385$lambda$384$lambda$383$lambda$382$lambda$381$lambda$380($palette, $onDescriptionChange, $albumDescriptionField$delegate, $descriptionScrollState, arg_0, arg_1, arg_2), (Composer)$composer\42, (int)54)), (Composer)$composer\42, (int)196614, (int)24);
            if ($showOrganizeToggle) {
                $composer\42.startReplaceGroup(-1319677132);
                ComposerKt.sourceInformation((Composer)$composer\42, (String)"2194@101706L272");
                int n35 = 16;
                boolean bl51 = false;
                GroupAutoClassifySupportKt.GroupAutoClassifyToggleRow($autoClassifyEnabled, (Function1<? super Boolean, Unit>)$onAutoClassifyEnabledChange, PaddingKt.padding-qDBjuR0$default((Modifier)((Modifier)Modifier.Companion), (float)0.0f, (float)Dp.constructor-impl((float)n35), (float)0.0f, (float)0.0f, (int)13, null), (Composer)$composer\42, 384, 0);
                $composer\42.endReplaceGroup();
            } else {
                $composer\42.startReplaceGroup(-1420550884);
                $composer\42.endReplaceGroup();
            }
            float n35 = $adaptive.getPageBottomPadding-D9Ej5fM();
            int $this$dp\5222 = 6;
            boolean bl52 = false;
            float $this$dp\5222 = Dp.constructor-impl((float)$this$dp\5222);
            boolean bl53 = false;
            SpacerKt.Spacer((Modifier)SizeKt.height-3ABfNKs((Modifier)((Modifier)Modifier.Companion), (float)Dp.constructor-impl((float)(arg0\52 + other\52))), (Composer)$composer\42, (int)0);
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\42);
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\41);
            $composer\35.endNode();
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\35);
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\35);
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\35);
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\12);
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\11);
            $composer\5.endNode();
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\5);
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\5);
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\5);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        } else {
            $composer.skipToGroupEnd();
        }
        return Unit.INSTANCE;
    }

    @Composable
    @ComposableTarget(applier="androidx.compose.ui.UiComposable")
    private static final Unit GroupEditAlbumSheet$lambda$385$lambda$384(NoMemoSheetDragController $sheetDrag, NoMemoAdaptiveSpec $adaptive, long $panelSurface, float $bodyHeight, long $dragHandleColor, Function0 $requestConfirm, Function0 $tryDismiss, String $title, NoMemoPalette $palette, long $inputSurface, boolean $showOrganizeToggle, boolean $autoClassifyEnabled, Function1 $onAutoClassifyEnabledChange, Function1 $onNameChange, MutableState $albumNameField$delegate, Function1 $onDescriptionChange, MutableState $albumDescriptionField$delegate, ScrollState $descriptionScrollState, AnimatedVisibilityScope $this$AnimatedVisibility, Composer $composer, int $changed) {
        float f;
        Intrinsics.checkNotNullParameter((Object)$this$AnimatedVisibility, (String)"$this$AnimatedVisibility");
        ComposerKt.sourceInformation((Composer)$composer, (String)"C2046@94381L41,2047@94437L7716,2036@93881L8272:GroupActivity.kt#83vr7l");
        if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart((int)-1772561400, (int)$changed, (int)-1, (String)"com.han.nomemo.GroupActivity.GroupEditAlbumSheet.<anonymous>.<anonymous> (GroupActivity.kt:2036)");
        }
        Modifier modifier = SizeKt.fillMaxWidth$default((Modifier)WindowInsetsPadding_androidKt.imePadding((Modifier)ComposeUiKt.noMemoSheetDragOffset((Modifier)Modifier.Companion, $sheetDrag)), (float)0.0f, (int)1, null);
        if ($adaptive.isNarrow()) {
            int n = 18;
            boolean bl = false;
            f = Dp.constructor-impl((float)n);
        } else {
            int n = 24;
            boolean bl = false;
            f = Dp.constructor-impl((float)n);
        }
        int n = 36;
        boolean bl = false;
        int n2 = 36;
        boolean bl2 = false;
        int n3 = 36;
        boolean bl3 = false;
        int n4 = 36;
        boolean bl4 = false;
        CardKt.Card((Modifier)ShadowKt.shadow-s4CzXII$default((Modifier)modifier, (float)f, (Shape)((Shape)ComposeUiKt.noMemoG2RoundedShape-a9UjIt4$default(Dp.constructor-impl((float)n), Dp.constructor-impl((float)n2), 0.0f, 0.0f, 12, null)), (boolean)false, (long)0L, (long)0L, (int)28, null), (Shape)((Shape)ComposeUiKt.noMemoG2RoundedShape-a9UjIt4$default(Dp.constructor-impl((float)n3), Dp.constructor-impl((float)n4), 0.0f, 0.0f, 12, null)), (CardColors)CardDefaults.INSTANCE.cardColors-ro_MJ88($panelSurface, 0L, 0L, 0L, $composer, CardDefaults.$stable << 12, 14), null, null, (Function3)((Function3)ComposableLambdaKt.rememberComposableLambda((int)1499512954, (boolean)true, (arg_0, arg_1, arg_2) -> GroupActivity.GroupEditAlbumSheet$lambda$385$lambda$384$lambda$383($bodyHeight, $dragHandleColor, $sheetDrag, $adaptive, $requestConfirm, $tryDismiss, $title, $palette, $inputSurface, $showOrganizeToggle, $autoClassifyEnabled, $onAutoClassifyEnabledChange, $onNameChange, $albumNameField$delegate, $onDescriptionChange, $albumDescriptionField$delegate, $descriptionScrollState, arg_0, arg_1, arg_2), (Composer)$composer, (int)54)), (Composer)$composer, (int)196608, (int)24);
        if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
        }
        return Unit.INSTANCE;
    }

    private static final Unit GroupEditAlbumSheet$lambda$386(GroupActivity $tmp13_rcvr, BoxScope $this_GroupEditAlbumSheet, String $title, String $albumName, String $albumDescription, boolean $showOrganizeToggle, boolean $autoClassifyEnabled, Function1 $onAutoClassifyEnabledChange, Function1 $onNameChange, Function1 $onDescriptionChange, Function0 $onDismiss, Function0 $onConfirm, int $$changed, int $$changed1, Composer $composer, int $force) {
        $tmp13_rcvr.GroupEditAlbumSheet($this_GroupEditAlbumSheet, $title, $albumName, $albumDescription, $showOrganizeToggle, $autoClassifyEnabled, (Function1<? super Boolean, Unit>)$onAutoClassifyEnabledChange, (Function1<? super String, Unit>)$onNameChange, (Function1<? super String, Unit>)$onDescriptionChange, (Function0<Unit>)$onDismiss, (Function0<Boolean>)$onConfirm, $composer, RecomposeScopeImplKt.updateChangedFlags((int)($$changed | 1)), RecomposeScopeImplKt.updateChangedFlags((int)$$changed1));
        return Unit.INSTANCE;
    }

    /*
     * WARNING - void declaration
     */
    @Composable
    @ComposableInferredTarget(scheme="[androidx.compose.ui.UiComposable[androidx.compose.ui.UiComposable]]")
    private static final Unit GroupAlbumInputField_hGBTI10$lambda$389$lambda$388(String $value, String $placeholder, NoMemoPalette $palette, Function2 innerTextField, Composer $composer, int $changed) {
        Intrinsics.checkNotNullParameter((Object)innerTextField, (String)"innerTextField");
        ComposerKt.sourceInformation((Composer)$composer, (String)"CN(innerTextField)2254@103759L468:GroupActivity.kt#83vr7l");
        int $dirty = $changed;
        if (($changed & 6) == 0) {
            $dirty |= $composer.changedInstance((Object)innerTextField) ? 4 : 2;
        }
        if ($composer.shouldExecute(($dirty & 0x13) != 18, $dirty & 1)) {
            void $composer\8;
            void $changed\3;
            void $changed\2;
            void modifier\2;
            void $changed\1;
            void modifier\1;
            void contentAlignment\1;
            void $composer\1;
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart((int)-1489393946, (int)$dirty, (int)-1, (String)"com.han.nomemo.GroupActivity.GroupAlbumInputField.<anonymous>.<anonymous> (GroupActivity.kt:2254)");
            }
            Modifier modifier = SizeKt.fillMaxWidth$default((Modifier)((Modifier)Modifier.Companion), (float)0.0f, (int)1, null);
            Alignment alignment = Alignment.Companion.getCenterStart();
            Composer composer = $composer;
            int n = 54;
            boolean bl = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\1, (int)1042775818, (String)"CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo");
            boolean bl2 = false;
            MeasurePolicy measurePolicy = BoxKt.maybeCachedBoxMeasurePolicy((Alignment)contentAlignment\1, (boolean)bl2);
            void var14_14 = modifier\1;
            int n2 = 0x70 & $changed\1 << 3;
            boolean bl3 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\1, (int)-1159599143, (String)"CC(Layout)P(!1,2)80@3267L27,83@3433L360:Layout.kt#80mrfh");
            int n3 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode((Composer)$composer\1, (int)0));
            CompositionLocalMap compositionLocalMap = $composer\1.getCurrentCompositionLocalMap();
            Modifier modifier2 = ComposedModifierKt.materializeModifier((Composer)$composer\1, (Modifier)modifier\2);
            Function0 function0 = ComposeUiNode.Companion.getConstructor();
            int n4 = 6 | 0x380 & $changed\2 << 6;
            boolean bl4 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\1, (int)-553112988, (String)"CC(ReusableComposeNode)N(factory,update,content)399@15590L9:Composables.kt#9igjgp");
            if (!($composer\1.getApplier() instanceof Applier)) {
                ComposablesKt.invalidApplier();
            }
            $composer\1.startReusableNode();
            if ($composer\1.getInserting()) {
                void factory\3;
                $composer\1.createNode((Function0)factory\3);
            } else {
                $composer\1.useNode();
            }
            Composer composer2 = Updater.constructor-impl((Composer)$composer\1);
            boolean bl5 = false;
            Updater.set-impl((Composer)composer2, (Object)measurePolicy, (Function2)ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.set-impl((Composer)composer2, (Object)compositionLocalMap, (Function2)ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            Function2 function2 = ComposeUiNode.Companion.getSetCompositeKeyHash();
            boolean bl6 = false;
            Composer composer3 = composer2;
            boolean bl7 = false;
            if (composer3.getInserting() || !Intrinsics.areEqual((Object)composer3.rememberedValue(), (Object)n3)) {
                composer3.updateRememberedValue((Object)n3);
                composer2.apply((Object)n3, function2);
            }
            Updater.set-impl((Composer)composer2, (Object)modifier2, (Function2)ComposeUiNode.Companion.getSetModifier());
            int n5 = 0xE & $changed\3 >> 6;
            void $composer\7 = $composer\1;
            boolean bl8 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\7, (int)1833054614, (String)"C72@3469L9:Box.kt#2w3rfo");
            int n6 = 6 | 0x70 & $changed\1 >> 6;
            void var33_33 = $composer\7;
            BoxScope boxScope = (BoxScope)BoxScopeInstance.INSTANCE;
            boolean bl9 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\8, (int)-802050557, (String)"C2265@104193L16:GroupActivity.kt#83vr7l");
            if (StringsKt.isBlank((CharSequence)$value)) {
                $composer\8.startReplaceGroup(-802031524);
                ComposerKt.sourceInformation((Composer)$composer\8, (String)"2259@103968L182");
                TextKt.Text--4IGK_g((String)$placeholder, null, (long)$palette.getTextTertiary-0d7_KjU(), (long)TextUnitKt.getSp((int)14), null, null, null, (long)0L, null, null, (long)0L, (int)0, (boolean)false, (int)0, (int)0, null, null, (Composer)$composer\8, (int)3072, (int)0, (int)131058);
                $composer\8.endReplaceGroup();
            } else {
                $composer\8.startReplaceGroup(-905150110);
                $composer\8.endReplaceGroup();
            }
            innerTextField.invoke((Object)$composer\8, (Object)(0xE & $dirty));
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\8);
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\7);
            $composer\1.endNode();
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\1);
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\1);
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\1);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        } else {
            $composer.skipToGroupEnd();
        }
        return Unit.INSTANCE;
    }

    @Composable
    @ComposableTarget(applier="androidx.compose.ui.UiComposable")
    private static final Unit GroupAlbumInputField_hGBTI10$lambda$389(NoMemoPalette $palette, float $minHeight, String $value, Function1 $onValueChange, String $placeholder, ColumnScope $this$Card, Composer $composer, int $changed) {
        Intrinsics.checkNotNullParameter((Object)$this$Card, (String)"$this$Card");
        ComposerKt.sourceInformation((Composer)$composer, (String)"C2253@103723L518,2241@103245L996:GroupActivity.kt#83vr7l");
        if ($composer.shouldExecute(($changed & 0x11) != 16, $changed & 1)) {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart((int)-951448317, (int)$changed, (int)-1, (String)"com.han.nomemo.GroupActivity.GroupAlbumInputField.<anonymous> (GroupActivity.kt:2241)");
            }
            TextStyle textStyle = new TextStyle($palette.getTextPrimary-0d7_KjU(), TextUnitKt.getSp((int)15), null, null, null, null, null, 0L, null, null, null, 0L, null, null, null, 0, 0, TextUnitKt.getSp((int)22), null, null, null, 0, 0, null, 0xFDFFFC, null);
            int n = 14;
            boolean bl = false;
            int n2 = 12;
            boolean bl2 = false;
            Modifier modifier = PaddingKt.padding-VpY3zN4((Modifier)SizeKt.heightIn-VpY3zN4$default((Modifier)SizeKt.fillMaxWidth$default((Modifier)((Modifier)Modifier.Companion), (float)0.0f, (int)1, null), (float)$minHeight, (float)0.0f, (int)2, null), (float)Dp.constructor-impl((float)n), (float)Dp.constructor-impl((float)n2));
            BasicTextFieldKt.BasicTextField((String)$value, (Function1)$onValueChange, (Modifier)modifier, (boolean)false, (boolean)false, (TextStyle)textStyle, null, null, (boolean)false, (int)0, (int)0, null, null, null, null, (Function3)((Function3)ComposableLambdaKt.rememberComposableLambda((int)-1489393946, (boolean)true, (arg_0, arg_1, arg_2) -> GroupActivity.GroupAlbumInputField_hGBTI10$lambda$389$lambda$388($value, $placeholder, $palette, arg_0, arg_1, arg_2), (Composer)$composer, (int)54)), (Composer)$composer, (int)0, (int)196608, (int)32728);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        } else {
            $composer.skipToGroupEnd();
        }
        return Unit.INSTANCE;
    }

    private static final Unit GroupAlbumInputField_hGBTI10$lambda$390(GroupActivity $tmp0_rcvr, String $value, Function1 $onValueChange, String $placeholder, float $minHeight, Modifier $modifier, int $$changed, int $$default, Composer $composer, int $force) {
        $tmp0_rcvr.GroupAlbumInputField-hGBTI10($value, (Function1<? super String, Unit>)$onValueChange, $placeholder, $minHeight, $modifier, $composer, RecomposeScopeImplKt.updateChangedFlags((int)($$changed | 1)), $$default);
        return Unit.INSTANCE;
    }

    private static final Unit GroupAlbumDetailEmptyState$lambda$392(GroupActivity $tmp0_rcvr, boolean $organizeProcessing, Function0 $onAddMemoryClick, Function0 $onOrganizeClick, int $$changed, Composer $composer, int $force) {
        $tmp0_rcvr.GroupAlbumDetailEmptyState($organizeProcessing, (Function0<Unit>)$onAddMemoryClick, (Function0<Unit>)$onOrganizeClick, $composer, RecomposeScopeImplKt.updateChangedFlags((int)($$changed | 1)));
        return Unit.INSTANCE;
    }

    /*
     * WARNING - void declaration
     */
    private static final float GroupAlbumEmptyActionButton_DTcfvLk$lambda$393(State<Float> $alpha$delegate) {
        void $this$getValue\1;
        State<Float> state = $alpha$delegate;
        Object var2_2 = null;
        Object property\1 = null;
        boolean bl = false;
        return ((Number)$this$getValue\1.getValue()).floatValue();
    }

    /*
     * WARNING - void declaration
     */
    private static final boolean GroupAlbumEmptyActionButton_DTcfvLk$lambda$395(State<Boolean> $pressed$delegate) {
        void $this$getValue\1;
        State<Boolean> state = $pressed$delegate;
        Object var2_2 = null;
        Object property\1 = null;
        boolean bl = false;
        return (Boolean)$this$getValue\1.getValue();
    }

    /*
     * WARNING - void declaration
     */
    @Composable
    @ComposableTarget(applier="androidx.compose.ui.UiComposable")
    private static final Unit GroupAlbumEmptyActionButton_DTcfvLk$lambda$398$lambda$397(String $text, long $contentColor, ColumnScope $this$Card, Composer $composer, int $changed) {
        Intrinsics.checkNotNullParameter((Object)$this$Card, (String)"$this$Card");
        ComposerKt.sourceInformation((Composer)$composer, (String)"C2363@107847L448:GroupActivity.kt#83vr7l");
        if ($composer.shouldExecute(($changed & 0x11) != 16, $changed & 1)) {
            void $composer\9;
            void $changed\4;
            void $changed\3;
            void modifier\3;
            void $changed\2;
            void modifier\2;
            void contentAlignment\2;
            void $composer\2;
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart((int)-113129815, (int)$changed, (int)-1, (String)"com.han.nomemo.GroupActivity.GroupAlbumEmptyActionButton.<anonymous>.<anonymous> (GroupActivity.kt:2363)");
            }
            int $this$dp\22 = 54;
            boolean $i$f$getDp\1\23682 = false;
            Modifier $this$dp\22 = SizeKt.height-3ABfNKs((Modifier)SizeKt.fillMaxWidth$default((Modifier)((Modifier)Modifier.Companion), (float)0.0f, (int)1, null), (float)Dp.constructor-impl((float)$this$dp\22));
            Alignment $i$f$getDp\1\23682 = Alignment.Companion.getCenter();
            Composer composer = $composer;
            int n = 54;
            boolean bl = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\2, (int)1042775818, (String)"CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo");
            boolean bl2 = false;
            MeasurePolicy measurePolicy = BoxKt.maybeCachedBoxMeasurePolicy((Alignment)contentAlignment\2, (boolean)bl2);
            void var13_14 = modifier\2;
            int n2 = 0x70 & $changed\2 << 3;
            boolean bl3 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\2, (int)-1159599143, (String)"CC(Layout)P(!1,2)80@3267L27,83@3433L360:Layout.kt#80mrfh");
            int n3 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode((Composer)$composer\2, (int)0));
            CompositionLocalMap compositionLocalMap = $composer\2.getCurrentCompositionLocalMap();
            Modifier modifier = ComposedModifierKt.materializeModifier((Composer)$composer\2, (Modifier)modifier\3);
            Function0 function0 = ComposeUiNode.Companion.getConstructor();
            int n4 = 6 | 0x380 & $changed\3 << 6;
            boolean bl4 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\2, (int)-553112988, (String)"CC(ReusableComposeNode)N(factory,update,content)399@15590L9:Composables.kt#9igjgp");
            if (!($composer\2.getApplier() instanceof Applier)) {
                ComposablesKt.invalidApplier();
            }
            $composer\2.startReusableNode();
            if ($composer\2.getInserting()) {
                void factory\4;
                $composer\2.createNode((Function0)factory\4);
            } else {
                $composer\2.useNode();
            }
            Composer composer2 = Updater.constructor-impl((Composer)$composer\2);
            boolean bl5 = false;
            Updater.set-impl((Composer)composer2, (Object)measurePolicy, (Function2)ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.set-impl((Composer)composer2, (Object)compositionLocalMap, (Function2)ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            Function2 function2 = ComposeUiNode.Companion.getSetCompositeKeyHash();
            boolean bl6 = false;
            Composer composer3 = composer2;
            boolean bl7 = false;
            if (composer3.getInserting() || !Intrinsics.areEqual((Object)composer3.rememberedValue(), (Object)n3)) {
                composer3.updateRememberedValue((Object)n3);
                composer2.apply((Object)n3, function2);
            }
            Updater.set-impl((Composer)composer2, (Object)modifier, (Function2)ComposeUiNode.Companion.getSetModifier());
            int n5 = 0xE & $changed\4 >> 6;
            void $composer\8 = $composer\2;
            boolean bl8 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\8, (int)1833054614, (String)"C72@3469L9:Box.kt#2w3rfo");
            int n6 = 6 | 0x70 & $changed\2 >> 6;
            void var32_33 = $composer\8;
            BoxScope boxScope = (BoxScope)BoxScopeInstance.INSTANCE;
            boolean bl9 = false;
            ComposerKt.sourceInformationMarkerStart((Composer)$composer\9, (int)-1082555261, (String)"C2369@108068L209:GroupActivity.kt#83vr7l");
            TextKt.Text--4IGK_g((String)$text, null, (long)$contentColor, (long)TextUnitKt.getSp((int)15), null, (FontWeight)FontWeight.Companion.getSemiBold(), null, (long)0L, null, null, (long)0L, (int)0, (boolean)false, (int)0, (int)0, null, null, (Composer)$composer\9, (int)199680, (int)0, (int)131026);
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\9);
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\8);
            $composer\2.endNode();
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\2);
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\2);
            ComposerKt.sourceInformationMarkerEnd((Composer)$composer\2);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        } else {
            $composer.skipToGroupEnd();
        }
        return Unit.INSTANCE;
    }

    @Composable
    @ComposableTarget(applier="androidx.compose.ui.UiComposable")
    private static final Unit GroupAlbumEmptyActionButton_DTcfvLk$lambda$398(ContinuousRoundedRectangle $shape, long $effectiveContainerColor, String $text, long $contentColor, BoxScope boxScope, Composer $composer, int $changed) {
        Intrinsics.checkNotNullParameter((Object)boxScope, (String)"<this>");
        ComposerKt.sourceInformation((Composer)$composer, (String)"C2361@107762L52,2362@107829L480,2358@107635L674:GroupActivity.kt#83vr7l");
        if ($composer.shouldExecute(($changed & 0x11) != 16, $changed & 1)) {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart((int)-1724901705, (int)$changed, (int)-1, (String)"com.han.nomemo.GroupActivity.GroupAlbumEmptyActionButton.<anonymous> (GroupActivity.kt:2358)");
            }
            CardKt.Card((Modifier)SizeKt.fillMaxWidth$default((Modifier)((Modifier)Modifier.Companion), (float)0.0f, (int)1, null), (Shape)((Shape)$shape), (CardColors)CardDefaults.INSTANCE.cardColors-ro_MJ88($effectiveContainerColor, 0L, 0L, 0L, $composer, CardDefaults.$stable << 12, 14), null, null, (Function3)((Function3)ComposableLambdaKt.rememberComposableLambda((int)-113129815, (boolean)true, (arg_0, arg_1, arg_2) -> GroupActivity.GroupAlbumEmptyActionButton_DTcfvLk$lambda$398$lambda$397($text, $contentColor, arg_0, arg_1, arg_2), (Composer)$composer, (int)54)), (Composer)$composer, (int)196614, (int)24);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        } else {
            $composer.skipToGroupEnd();
        }
        return Unit.INSTANCE;
    }

    private static final Unit GroupAlbumEmptyActionButton_DTcfvLk$lambda$400(GroupActivity $tmp1_rcvr, String $text, long $containerColor, long $contentColor, boolean $enabled, Function0 $onClick, int $$changed, int $$default, Composer $composer, int $force) {
        $tmp1_rcvr.GroupAlbumEmptyActionButton-DTcfvLk($text, $containerColor, $contentColor, $enabled, (Function0<Unit>)$onClick, $composer, RecomposeScopeImplKt.updateChangedFlags((int)($$changed | 1)), $$default);
        return Unit.INSTANCE;
    }

    private static final Unit GroupChip_a5Y__hM$lambda$401(GroupActivity $tmp0_rcvr, String $text, boolean $selected, long $chipTextSize, Function0 $onClick, int $$changed, Composer $composer, int $force) {
        $tmp0_rcvr.GroupChip-a5Y-_hM($text, $selected, $chipTextSize, (Function0<Unit>)$onClick, $composer, RecomposeScopeImplKt.updateChangedFlags((int)($$changed | 1)));
        return Unit.INSTANCE;
    }

    public static final /* synthetic */ MemoryStore access$getMemoryStore$p(GroupActivity $this) {
        return $this.memoryStore;
    }

    public static final /* synthetic */ void access$setAllRecords(GroupActivity $this, List list) {
        $this.setAllRecords(list);
    }

    public static final /* synthetic */ void access$setHasLoadedRecords(GroupActivity $this, boolean bl) {
        $this.setHasLoadedRecords(bl);
    }

    public static final /* synthetic */ void access$GroupContent$lambda$35(MutableState $albumList$delegate, List list) {
        GroupActivity.GroupContent$lambda$35((MutableState<List<GroupAlbumStore.GroupAlbum>>)$albumList$delegate, list);
    }

    public static final /* synthetic */ void access$GroupContent$lambda$65(MutableState $selectedAlbumRecordIds$delegate, Set set) {
        GroupActivity.GroupContent$lambda$65((MutableState<Set<String>>)$selectedAlbumRecordIds$delegate, set);
    }

    public static final /* synthetic */ void access$GroupContent$lambda$68(MutableState $albumSelectionModeActive$delegate, boolean bl) {
        GroupActivity.GroupContent$lambda$68((MutableState<Boolean>)$albumSelectionModeActive$delegate, bl);
    }

    public static final /* synthetic */ void access$GroupContent$lambda$71(MutableState $showRemoveFromAlbumConfirm$delegate, boolean bl) {
        GroupActivity.GroupContent$lambda$71((MutableState<Boolean>)$showRemoveFromAlbumConfirm$delegate, bl);
    }

    public static final /* synthetic */ void access$GroupContent$lambda$74(MutableState $showDeleteSelectedConfirm$delegate, boolean bl) {
        GroupActivity.GroupContent$lambda$74((MutableState<Boolean>)$showDeleteSelectedConfirm$delegate, bl);
    }

    public static final /* synthetic */ Set access$GroupContent$lambda$64(MutableState $selectedAlbumRecordIds$delegate) {
        return GroupActivity.GroupContent$lambda$64((MutableState<Set<String>>)$selectedAlbumRecordIds$delegate);
    }

    public static final /* synthetic */ String access$GroupContent$lambda$37(MutableState $openedAlbumId$delegate) {
        return GroupActivity.GroupContent$lambda$37((MutableState<String>)$openedAlbumId$delegate);
    }

    public static final /* synthetic */ void access$GroupContent$lambda$53(MutableState $groupListMoreExpanded$delegate, boolean bl) {
        GroupActivity.GroupContent$lambda$53((MutableState<Boolean>)$groupListMoreExpanded$delegate, bl);
    }

    public static final /* synthetic */ void access$GroupAlbumGridCard(GroupActivity $this, GroupAlbumStore.GroupAlbum album, boolean compact, int memoryCount, List previewRecords, Modifier modifier, Function0 onClick, Composer $composer, int $changed, int $default) {
        $this.GroupAlbumGridCard(album, compact, memoryCount, previewRecords, modifier, (Function0<Unit>)onClick, $composer, $changed, $default);
    }

    public static final /* synthetic */ boolean access$GroupContent$lambda$67(MutableState $albumSelectionModeActive$delegate) {
        return GroupActivity.GroupContent$lambda$67((MutableState<Boolean>)$albumSelectionModeActive$delegate);
    }

    public static final /* synthetic */ void access$GroupContent$lambda$56(MutableState $detailMoreExpanded$delegate, boolean bl) {
        GroupActivity.GroupContent$lambda$56((MutableState<Boolean>)$detailMoreExpanded$delegate, bl);
    }

    public static final /* synthetic */ void access$GroupAddExistingMemorySheet$lambda$290(MutableState $visible$delegate, boolean bl) {
        GroupActivity.GroupAddExistingMemorySheet$lambda$290((MutableState<Boolean>)$visible$delegate, bl);
    }

    public static final /* synthetic */ boolean access$GroupAddExistingMemorySheet$lambda$289(MutableState $visible$delegate) {
        return GroupActivity.GroupAddExistingMemorySheet$lambda$289((MutableState<Boolean>)$visible$delegate);
    }

    public static final /* synthetic */ boolean access$GroupAddExistingMemorySheet$lambda$292(MutableState $dismissCommitted$delegate) {
        return GroupActivity.GroupAddExistingMemorySheet$lambda$292((MutableState<Boolean>)$dismissCommitted$delegate);
    }

    public static final /* synthetic */ void access$GroupAddExistingMemorySheet$lambda$293(MutableState $dismissCommitted$delegate, boolean bl) {
        GroupActivity.GroupAddExistingMemorySheet$lambda$293((MutableState<Boolean>)$dismissCommitted$delegate, bl);
    }

    public static final /* synthetic */ TextFieldValue access$GroupEditAlbumSheet$lambda$341(MutableState $albumNameField$delegate) {
        return GroupActivity.GroupEditAlbumSheet$lambda$341((MutableState<TextFieldValue>)$albumNameField$delegate);
    }

    public static final /* synthetic */ void access$GroupEditAlbumSheet$lambda$342(MutableState $albumNameField$delegate, TextFieldValue textFieldValue) {
        GroupActivity.GroupEditAlbumSheet$lambda$342((MutableState<TextFieldValue>)$albumNameField$delegate, textFieldValue);
    }

    public static final /* synthetic */ TextFieldValue access$GroupEditAlbumSheet$lambda$344(MutableState $albumDescriptionField$delegate) {
        return GroupActivity.GroupEditAlbumSheet$lambda$344((MutableState<TextFieldValue>)$albumDescriptionField$delegate);
    }

    public static final /* synthetic */ void access$GroupEditAlbumSheet$lambda$345(MutableState $albumDescriptionField$delegate, TextFieldValue textFieldValue) {
        GroupActivity.GroupEditAlbumSheet$lambda$345((MutableState<TextFieldValue>)$albumDescriptionField$delegate, textFieldValue);
    }

    public static final /* synthetic */ void access$GroupEditAlbumSheet$lambda$336(MutableState $visible$delegate, boolean bl) {
        GroupActivity.GroupEditAlbumSheet$lambda$336((MutableState<Boolean>)$visible$delegate, bl);
    }

    public static final /* synthetic */ boolean access$GroupEditAlbumSheet$lambda$335(MutableState $visible$delegate) {
        return GroupActivity.GroupEditAlbumSheet$lambda$335((MutableState<Boolean>)$visible$delegate);
    }

    public static final /* synthetic */ boolean access$GroupEditAlbumSheet$lambda$338(MutableState $dismissCommitted$delegate) {
        return GroupActivity.GroupEditAlbumSheet$lambda$338((MutableState<Boolean>)$dismissCommitted$delegate);
    }

    public static final /* synthetic */ void access$GroupEditAlbumSheet$lambda$339(MutableState $dismissCommitted$delegate, boolean bl) {
        GroupActivity.GroupEditAlbumSheet$lambda$339((MutableState<Boolean>)$dismissCommitted$delegate, bl);
    }

    public static final /* synthetic */ void access$refreshContent(GroupActivity $this) {
        $this.refreshContent();
    }

    public static final /* synthetic */ void access$setAlbumRefreshTick(GroupActivity $this, int n) {
        $this.setAlbumRefreshTick(n);
    }

    public static final /* synthetic */ int access$getAlbumRefreshTick(GroupActivity $this) {
        return $this.getAlbumRefreshTick();
    }
}
