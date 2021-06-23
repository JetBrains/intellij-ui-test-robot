// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

export declare let robot: Robot
export declare let component: Component
export declare let log: Log
export declare let local: Map
export declare let global: Map


export interface Robot {
    showWindow(window: Window, dimension: Dimension, arg2: boolean)

    showWindow(window: Window)

    showWindow(window: Window, dimension: Dimension)

    pressKeyWhileRunning(arg0: number, runnable: Runnable)

    isActive(): boolean

    waitForIdle()

    pressAndReleaseKey(arg0: number, arg1: number[])

    showPopupMenu(component: Component): JPopupMenu

    showPopupMenu(component: Component, point: Point): JPopupMenu

    jitter(component: Component)

    jitter(component: Component, point: Point)

    pressModifiers(arg0: number)

    pressMouse(point: Point, mousebutton: MouseButton)

    pressMouse(component: Component, point: Point, mousebutton: MouseButton)

    pressMouse(component: Component, point: Point)

    pressMouse(mousebutton: MouseButton)

    pressMouseWhileRunning(component: Component, point: Point, runnable: Runnable)

    pressMouseWhileRunning(component: Component, point: Point, mousebutton: MouseButton, runnable: Runnable)

    pressMouseWhileRunning(point: Point, mousebutton: MouseButton, runnable: Runnable)

    pressMouseWhileRunning(mousebutton: MouseButton, runnable: Runnable)

    hierarchy(): ComponentHierarchy

    releaseKey(arg0: number)

    pressModifiersWhileRunning(arg0: number, runnable: Runnable)

    isDragging(): boolean

    requireNoJOptionPaneIsShowing()

    printer(): ComponentPrinter

    type(arg0: string)

    close(window: Window)

    releaseMouse(mousebutton: MouseButton)

    cleanUp()

    moveMouse(component: Component, point: Point)

    moveMouse(point: Point)

    moveMouse(arg0: number, arg1: number)

    moveMouse(component: Component, arg1: number, arg2: number)

    moveMouse(component: Component)

    pressKey(arg0: number)

    settings(): Settings

    click(component: Component, mousebutton: MouseButton, arg2: number)

    click(component: Component, mousebutton: MouseButton)

    click(component: Component)

    click(point: Point, mousebutton: MouseButton, arg2: number)

    click(component: Component, point: Point)

    click(component: Component, point: Point, mousebutton: MouseButton, arg3: number)

    enterText(arg0: string)

    releaseMouseButtons()

    rightClick(component: Component)

    focus(component: Component)

    doubleClick(component: Component)

    cleanUpWithoutDisposingWindows()

    isReadyForInput(component: Component): boolean

    focusAndWaitForFocusGain(component: Component)

    releaseModifiers(arg0: number)

    findActivePopupMenu(): JPopupMenu

    rotateMouseWheel(arg0: number)

    rotateMouseWheel(component: Component, arg1: number)

    pressAndReleaseKeys(arg0: number[])

    finder(): ComponentFinder
}

export class MouseButton {
    static LEFT_BUTTON
    static MIDDLE_BUTTON
    static RIGHT_BUTTON
}

export interface Log {
    info(message)

    warn(message)
}

export interface Map {
    put(key, value)

    get(key)
}


export interface Dimension {
}

export interface Runnable {
    run()
}

export interface Component {
    areInputMethodsEnabled(): boolean

    postsOldMouseEvents(): boolean

    numListening(arg0: number): number

    isPreferredSizeSet(): boolean

    setMinimumSize(dimension: Dimension)

    createBufferStrategy(arg0: number)

    createBufferStrategy(arg0: number, buffercapabilities: BufferCapabilities)

    createHierarchyEvents(arg0: number, component2: Component, container: Container, arg3: number, arg4: boolean): number

    keyDown(event: Event, arg1: number): boolean

    getMouseListeners(): MouseListener[]

    getLocation(): Point

    getLocation(point: Point): Point

    getHeight(): number

    show()

    show(arg0: boolean)

    getMousePosition(): Point

    getGraphicsConfiguration(): GraphicsConfiguration

    updateCursorImmediately()

    disableEvents(arg0: number)

    getAccessibleIndexInParent(): number

    getIgnoreRepaint(): boolean

    hasFocus(): boolean

    createImage(imageproducer: ImageProducer): Image

    createImage(arg0: number, arg1: number): Image

    coalesceEvents(awtevent1: AWTEvent, awtevent2: AWTEvent): AWTEvent

    setBoundsOp(arg0: number)

    paramString(): string

    isBackgroundSet(): boolean

    notifyNewBounds(arg0: boolean, arg1: boolean)

    getSiblingIndexAbove(): number

    getBackBuffer(): Image

    transferFocus(arg0: boolean): boolean

    transferFocus()

    dispatchMouseWheelToAncestor(mousewheelevent: MouseWheelEvent): boolean

    isVisible(): boolean

    disable()

    requestFocusHelper(arg0: boolean, arg1: boolean): boolean

    requestFocusHelper(arg0: boolean, arg1: boolean, cause: Cause): boolean

    findUnderMouseInWindow(pointerinfo: PointerInfo): Component

    contains(point: Point): boolean

    contains(arg0: number, arg1: number): boolean

    addComponentListener(componentlistener: ComponentListener)

    setIgnoreRepaint(arg0: boolean)

    processInputMethodEvent(inputmethodevent: InputMethodEvent)

    setBounds(rectangle: Rectangle)

    setBounds(arg0: number, arg1: number, arg2: number, arg3: number)

    action(event: Event, arg1: any): boolean

    isFocusCycleRoot(container: Container): boolean

    getBounds(rectangle: Rectangle): Rectangle

    getBounds(): Rectangle

    invalidateIfValid()

    eventTypeEnabled(arg0: number): boolean

    location(): Point

    removeHierarchyBoundsListener(hierarchyboundslistener: HierarchyBoundsListener)

    update(graphics: Graphics)

    addFocusListener(focuslistener: FocusListener)

    mouseEnter(event: Event, arg1: number, arg2: number): boolean

    isDisplayable(): boolean

    isSameOrAncestorOf(component: Component, arg1: boolean): boolean

    updateGraphicsData(graphicsconfiguration: GraphicsConfiguration): boolean

    mouseExit(event: Event, arg1: number, arg2: number): boolean

    enableInputMethods(arg0: boolean)

    addMouseMotionListener(mousemotionlistener: MouseMotionListener)

    setForeground(color: Color)

    isValid(): boolean

    minimumSize(): Dimension

    removeMouseListener(mouselistener: MouseListener)

    getSiblingIndexBelow(): number

    validate()

    setGraphicsConfiguration(graphicsconfiguration: GraphicsConfiguration)

    mixOnValidating()

    getBoundsOp(): number

    mixOnZOrderChanging(arg0: number, arg1: number)

    removeHierarchyListener(hierarchylistener: HierarchyListener)

    setFocusable(arg0: boolean)

    getInputContext(): InputContext

    move(arg0: number, arg1: number)

    containsFocus(): boolean

    revalidate()

    removePropertyChangeListener(arg0: string, propertychangelistener: PropertyChangeListener)

    removePropertyChangeListener(propertychangelistener: PropertyChangeListener)

    setBackground(color: Color)

    getBaseline(arg0: number, arg1: number): number

    setMaximumSize(dimension: Dimension)

    isVisible_NoClientCode(): boolean

    setFocusTraversalKeys_NoIDCheck(arg0: number, arg1: AWTKeyStroke[])

    clearCurrentFocusCycleRootOnHide()

    getLocationOnScreen_NoTreeLock(): Point

    paintHeavyweightComponents(graphics: Graphics)

    lostFocus(event: Event, arg1: any): boolean

    getAppliedShape(): Region

    isFontSet(): boolean

    getLocationOnScreen(): Point

    repaint(arg0: number)

    repaint(arg0: number, arg1: number, arg2: number, arg3: number, arg4: number)

    repaint(arg0: number, arg1: number, arg2: number, arg3: number)

    repaint()

    getContainingWindow(): Window

    size(): Dimension

    applyComponentOrientation(componentorientation: ComponentOrientation)

    isOpaque(): boolean

    lightweightPaint(graphics: Graphics)

    removeNotify()

    isMinimumSizeSet(): boolean

    autoProcessMouseWheel(mousewheelevent: MouseWheelEvent)

    subtractAndApplyShape(region: Region)

    addHierarchyBoundsListener(hierarchyboundslistener: HierarchyBoundsListener)

    getParent_NoClientCode(): Container

    resize(arg0: number, arg1: number)

    resize(dimension: Dimension)

    isShowing(): boolean

    setVisible(arg0: boolean)

    isEnabled(): boolean

    processMouseEvent(mouseevent: MouseEvent)

    isFocusTraversable(): boolean

    setFont(font: Font)

    applyCurrentShapeBelowMe()

    getFocusTraversalKeys(arg0: number): AWTKeyStroke[]

    requestFocus(arg0: boolean, cause: Cause): boolean

    requestFocus(arg0: boolean): boolean

    requestFocus(cause: Cause): boolean

    requestFocus()

    setCursor(cursor: Cursor)

    repaintParentIfNeeded(arg0: number, arg1: number, arg2: number, arg3: number)

    locate(arg0: number, arg1: number): Component

    getTreeLock(): any

    getMinimumSize(): Dimension

    requestFocusInWindow(arg0: boolean): boolean

    requestFocusInWindow(arg0: boolean, cause: Cause): boolean

    requestFocusInWindow(cause: Cause): boolean

    requestFocusInWindow(): boolean

    setLocation(arg0: number, arg1: number)

    setLocation(point: Point)

    applyCompoundShape(region: Region)

    subtractAndApplyShapeBelowMe()

    enable(arg0: boolean)

    enable()

    processHierarchyEvent(hierarchyevent: HierarchyEvent)

    nextFocus()

    isMaximumSizeSet(): boolean

    getMouseMotionListeners(): MouseMotionListener[]

    mixOnHiding(arg0: boolean)

    lightweightPrint(graphics: Graphics)

    removeComponentListener(componentlistener: ComponentListener)

    reshapeNativePeer(arg0: number, arg1: number, arg2: number, arg3: number, arg4: number)

    addPropertyChangeListener(propertychangelistener: PropertyChangeListener)

    addPropertyChangeListener(arg0: string, propertychangelistener: PropertyChangeListener)

    getParent(): Container

    remove(menucomponent: MenuComponent)

    isLightweight(): boolean

    getFontMetrics(font: Font): FontMetrics

    processComponentEvent(componentevent: ComponentEvent)

    getNativeContainer(): Container

    getDropTarget(): DropTarget

    setName(arg0: string)

    processMouseWheelEvent(mousewheelevent: MouseWheelEvent)

    applyCurrentShape()

    initializeFocusTraversalKeys()

    getPropertyChangeListeners(arg0: string): PropertyChangeListener[]

    getPropertyChangeListeners(): PropertyChangeListener[]

    removeInputMethodListener(inputmethodlistener: InputMethodListener)

    canBeFocusOwner(): boolean

    list()

    list(printstream: PrintStream, arg1: number)

    list(printstream: PrintStream)

    list(printwriter: PrintWriter)

    list(printwriter: PrintWriter, arg1: number)

    isFocusTraversableOverridden(): boolean

    getGraphics_NoClientCode(): Graphics

    processEvent(awtevent: AWTEvent)

    removeMouseMotionListener(mousemotionlistener: MouseMotionListener)

    canBeFocusOwnerRecursively(): boolean

    getHierarchyBoundsListeners(): HierarchyBoundsListener[]

    getNextFocusCandidate(): Component

    getInputMethodListeners(): InputMethodListener[]

    invalidate()

    doLayout()

    removeMouseWheelListener(mousewheellistener: MouseWheelListener)

    hide()

    getBackground(): Color

    setEnabled(arg0: boolean)

    paintAll(graphics: Graphics)

    isRecursivelyVisible(): boolean

    deliverEvent(event: Event)

    setDropTarget(droptarget: DropTarget)

    checkGD(arg0: string)

    removeKeyListener(keylistener: KeyListener)

    processMouseMotionEvent(mouseevent: MouseEvent)

    getBufferStrategy(): BufferStrategy

    isAutoFocusTransferOnDisposal(): boolean

    getAccessibleStateSet(): AccessibleStateSet

    readObject(objectinputstream: ObjectInputStream)

    printHeavyweightComponents(graphics: Graphics)

    isCoalescingEnabled(): boolean

    inside(arg0: number, arg1: number): boolean

    removeFocusListener(focuslistener: FocusListener)

    getOpaqueShape(): Region

    getPreferredSize(): Dimension

    isNonOpaqueForMixing(): boolean

    reshape(arg0: number, arg1: number, arg2: number, arg3: number)

    mouseDrag(event: Event, arg1: number, arg2: number): boolean

    getTraversalRoot(): Container

    relocateComponent()

    getLocationOnWindow(): Point

    setPreferredSize(dimension: Dimension)

    createVolatileImage(arg0: number, arg1: number): VolatileImage

    createVolatileImage(arg0: number, arg1: number, imagecapabilities: ImageCapabilities): VolatileImage

    mouseUp(event: Event, arg1: number, arg2: number): boolean

    areBoundsValid(): boolean

    getAlignmentX(): number

    invalidateParent()

    getHWPeerAboveMe(): ComponentPeer

    getY(): number

    revalidateSynchronously()

    getLocale(): Locale

    getSize(dimension: Dimension): Dimension

    getSize(): Dimension

    getGraphicsConfiguration_NoClientCode(): GraphicsConfiguration

    setLocale(locale: Locale)

    updateZOrder()

    add(popupmenu: PopupMenu)

    isFocusOwner(): boolean

    getComponentAt(point: Point): Component

    getComponentAt(arg0: number, arg1: number): Component

    isRequestFocusAccepted(arg0: boolean, arg1: boolean, cause: Cause): boolean

    addNotify()

    setAutoFocusTransferOnDisposal(arg0: boolean)

    getAlignmentY(): number

    addInputMethodListener(inputmethodlistener: InputMethodListener)

    print(graphics: Graphics)

    getCursor_NoClientCode(): Cursor

    mouseDown(event: Event, arg1: number, arg2: number): boolean

    mixOnReshaping()

    setSize(arg0: number, arg1: number)

    setSize(dimension: Dimension)

    checkWindowClosingException(): boolean

    writeObject(objectoutputstream: ObjectOutputStream)

    setComponentOrientation(componentorientation: ComponentOrientation)

    getBaselineResizeBehavior(): BaselineResizeBehavior

    setFocusTraversalKeys(arg0: number, arg1: AWTKeyStroke[])

    checkImage(image: Image, imageobserver: ImageObserver): number

    checkImage(image: Image, arg1: number, arg2: number, imageobserver: ImageObserver): number

    processKeyEvent(keyevent: KeyEvent)

    getForeground(): Color

    getContainer(): Container

    getHierarchyListeners(): HierarchyListener[]

    prepareImage(image: Image, imageobserver: ImageObserver): boolean

    prepareImage(image: Image, arg1: number, arg2: number, imageobserver: ImageObserver): boolean

    eventEnabled(awtevent: AWTEvent): boolean

    getInputMethodRequests(): InputMethodRequests

    transferFocusUpCycle()

    checkTreeLock()

    isMixingNeeded(): boolean

    getAccessControlContext(): AccessControlContext

    clearMostRecentFocusOwnerOnHide()

    getFocusCycleRootAncestor(): Container

    calculateCurrentShape(): Region

    preferredSize(): Dimension

    layout()

    processHierarchyBoundsEvent(hierarchyevent: HierarchyEvent)

    getMouseWheelListeners(): MouseWheelListener[]

    pointRelativeToComponent(point: Point): Point

    getGraphics(): Graphics

    getFocusTraversalKeysEnabled(): boolean

    getX(): number

    adjustListeningChildrenOnParent(arg0: number, arg1: number)

    toString(): string

    getObjectLock(): any

    getInsets_NoClientCode(): Insets

    addHierarchyListener(hierarchylistener: HierarchyListener)

    enableEvents(arg0: number)

    getToolkitImpl(): Toolkit

    isFocusable(): boolean

    handleEvent(event: Event): boolean

    getFont(): Font

    mouseMove(event: Event, arg1: number, arg2: number): boolean

    keyUp(event: Event, arg1: number): boolean

    constructComponentName(): string

    getToolkit(): Toolkit

    isDoubleBuffered(): boolean

    isCursorSet(): boolean

    getRecursivelyVisibleBounds(): Rectangle

    getAccessibleContext(): AccessibleContext

    paint(graphics: Graphics)

    getName(): string

    addMouseWheelListener(mousewheellistener: MouseWheelListener)

    getComponentListeners(): ComponentListener[]

    countHierarchyMembers(): number

    getListeners<T extends EventListener>(arg0: Class<T>): T[]

    mixOnShowing()

    areFocusTraversalKeysSet(arg0: number): boolean

    getMaximumSize(): Dimension

    getCursor(): Cursor

    firePropertyChange(arg0: string, arg1: number, arg2: number)

    firePropertyChange(arg0: string, arg1: number, arg2: number)

    firePropertyChange(arg0: string, arg1: boolean, arg2: boolean)

    firePropertyChange(arg0: string, arg1: number, arg2: number)

    firePropertyChange(arg0: string, arg1: number, arg2: number)

    firePropertyChange(arg0: string, arg1: any, arg2: any)

    firePropertyChange(arg0: string, arg1: number, arg2: number)

    firePropertyChange(arg0: string, arg1: number, arg2: number)

    firePropertyChange(arg0: string, arg1: string, arg2: string)

    addKeyListener(keylistener: KeyListener)

    postEvent(event: Event): boolean

    imageUpdate(image: Image, arg1: number, arg2: number, arg3: number, arg4: number, arg5: number): boolean

    getColorModel(): ColorModel

    isEnabledImpl(): boolean

    bounds(): Rectangle

    dispatchEvent(awtevent: AWTEvent)

    doSwingSerialization()

    getPeer(): ComponentPeer

    getFocusListeners(): FocusListener[]

    transferFocusBackward()

    transferFocusBackward(arg0: boolean): boolean

    isForegroundSet(): boolean

    getKeyListeners(): KeyListener[]

    gotFocus(event: Event, arg1: any): boolean

    getWidth(): number

    addMouseListener(mouselistener: MouseListener)

    getComponentOrientation(): ComponentOrientation

    getFocusTraversalKeys_NoIDCheck(arg0: number): AWTKeyStroke[]

    dispatchEventImpl(awtevent: AWTEvent)

    setFocusTraversalKeysEnabled(arg0: boolean)

    getNormalShape(): Region

    processFocusEvent(focusevent: FocusEvent)

    checkCoalescing(): boolean

    isCoalesceEventsOverriden(arg0: Class<any>): boolean

    setRequestFocusController(requestfocuscontroller: RequestFocusController)

    isInstanceOf(arg0: any, arg1: string): boolean

    getVisibleRect(): Rectangle

    initIDs()

    foreground: Color;
    background: Color;
    font: Font;
    visible: boolean;
    enabled: boolean;
    name: string;
    focusable: boolean;
}

export interface HierarchyBoundsListener extends EventListener {
}

export interface InputMethodListener extends EventListener {
}

export interface VolatileImage extends Image, Transparency {
    getHeight(): number

    getSource(): ImageProducer

    getTransparency(): number

    getSnapshot(): BufferedImage

    getWidth(): number
}

export interface BufferStrategy {
}

export interface Transparency {
    getTransparency(): number
}

export interface MenuItem extends MenuComponent, Accessible {

    processActionEvent(actionevent: ActionEvent)


    setLabel(arg0: string)

    getShortcutMenuItem(menushortcut: MenuShortcut): MenuItem

    setEnabled(arg0: boolean)


    getActionCommand(): string

    isItemEnabled(): boolean

    getActionCommandImpl(): string

    isEnabled(): boolean

    paramString(): string

    constructComponentName(): string

    disable()

    getAccessibleContext(): AccessibleContext

    deleteShortcut()

    deleteShortcut(menushortcut: MenuShortcut)

    setActionCommand(arg0: string)

    enable()

    enable(arg0: boolean)

    getShortcut(): MenuShortcut

    handleShortcut(keyevent: KeyEvent): boolean

    doMenuEvent(arg0: number, arg1: number)

    setShortcut(menushortcut: MenuShortcut)

    getLabel(): string

    enabled: boolean;
    label: string;
    actionCommand: string;
    shortcut: MenuShortcut;
}

export interface ActionListener extends EventListener {
    actionPerformed(actionevent: ActionEvent)
}

export interface ActionEvent extends AWTEvent {
    getModifiers(): number

    getActionCommand(): string

    getWhen(): number

    paramString(): string

    actionCommand: string;
    when: number;
    modifiers: number;
}

export interface BufferedImage {

    getMinX(): number

    getRGB(arg0: number, arg1: number, arg2: number, arg3: number, arg4: number[], arg5: number, arg6: number): number[]

    getRGB(arg0: number, arg1: number): number

    getProperty(arg0: string, imageobserver: ImageObserver): any

    getProperty(arg0: string): any

    getSources(): RenderedImage[]

    hasTileWriters(): boolean

    getColorModel(): ColorModel

    isTileWritable(arg0: number, arg1: number): boolean

    getNumYTiles(): number

    getSampleModel(): SampleModel

    copyData(writableraster: WritableRaster): WritableRaster

    setRGB(arg0: number, arg1: number, arg2: number, arg3: number, arg4: number[], arg5: number, arg6: number)

    setRGB(arg0: number, arg1: number, arg2: number)

    getMinTileY(): number

    getPropertyNames(): string[]

    getGraphics(): Graphics

    getSubimage(arg0: number, arg1: number, arg2: number, arg3: number): BufferedImage

    getTileGridXOffset(): number

    getTileGridYOffset(): number

    getTileWidth(): number

    getHeight(): number

    getHeight(imageobserver: ImageObserver): number

    isAlphaPremultiplied(): boolean

    toString(): string

    getNumXTiles(): number

    getType(): number

    getTileHeight(): number

    getTransparency(): number

    getData(): Raster

    getData(rectangle: Rectangle): Raster

    getMinY(): number

    getTile(arg0: number, arg1: number): Raster

    getWidth(): number

    getWidth(imageobserver: ImageObserver): number

    removeTileObserver(tileobserver: TileObserver)

    getMinTileX(): number

    addTileObserver(tileobserver: TileObserver)

    createGraphics(): Graphics2D

    colorModel: ColorModel;
    raster: WritableRaster;
}

export interface Graphics2D extends Graphics {

}

export interface TileObserver {
    tileUpdate(writablerenderedimage: WritableRenderedImage, arg1: number, arg2: number, arg3: boolean)
}

export interface WritableRenderedImage extends RenderedImage {

}

export interface RenderedImage {
    getTileGridXOffset(): number

    getTileGridYOffset(): number

    getTileWidth(): number

    getHeight(): number

    getNumXTiles(): number

    getTileHeight(): number

    getMinX(): number

    getData(rectangle: Rectangle): Raster

    getData(): Raster

    getMinY(): number

    getTile(arg0: number, arg1: number): Raster

    getWidth(): number

    getProperty(arg0: string): any

    getSources(): RenderedImage[]

    getColorModel(): ColorModel

    getNumYTiles(): number

    getSampleModel(): SampleModel

    getMinTileX(): number

    copyData(writableraster: WritableRaster): WritableRaster

    getMinTileY(): number

    getPropertyNames(): string[]
}

export interface PointerInfo {
    getDevice(): GraphicsDevice

    getLocation(): Point

    device: GraphicsDevice;
    location: Point;
}


export interface GraphicsDevice {

}


export interface WritableRaster extends Raster {

}

export interface Raster {

}

export interface HierarchyBoundsListener extends EventListener {
    ancestorMoved(hierarchyevent: HierarchyEvent)

    ancestorResized(hierarchyevent: HierarchyEvent)
}

export interface SampleModel {

}

export interface PrintStream {

}

export interface HierarchyBoundsListener extends EventListener {
}


export interface MouseMotionListener extends EventListener {
    mouseMoved(mousemotionlistener: MouseMotionListener, mouseevent: MouseEvent)

    mouseDragged(mousemotionlistener: MouseMotionListener, mouseevent: MouseEvent)
}

export interface MenuComponent {

    getAccessibleContext(): AccessibleContext


    getName(): string


    getAccessControlContext(): AccessControlContext


    getAccessibleChildIndex(menucomponent2: MenuComponent): number


    getParent(): MenuContainer

    toString(): string


    getAccessibleStateSet(): AccessibleStateSet

    getAccessibleIndexInParent(): number


    setFont(font: Font)

    paramString(): string

    getFont(): Font

    setName(arg0: string)


}

export interface MenuComponentPeer {
    setFont(font: Font)

    dispose()
}

export interface MenuShortcut {

    usesShiftModifier(): boolean

    paramString(): string

    getKey(): number

    toString(m): string

    key: number;
}

export interface MenuContainer {

    getFont(): Font

    remove(menucomponent: MenuComponent)
}


export interface PopupMenu {
    show(component: Component, arg1: number, arg2: number)

    constructComponentName(): string

    getAccessibleContext(): AccessibleContext


    getParent(): MenuContainer
}

export class Cause {
    static UNKNOWN
    static MOUSE_EVENT
    static TRAVERSAL
    static TRAVERSAL_UP
    static TRAVERSAL_DOWN
    static TRAVERSAL_FORWARD
    static TRAVERSAL_BACKWARD
    static MANUAL_REQUEST
    static AUTOMATIC_TRAVERSE
    static ROLLBACK
    static NATIVE_SYSTEM
    static ACTIVATION
    static CLEAR_GLOBAL_FOCUS_OWNER
    static RETARGETED
}

export class BaselineResizeBehavior {
    static CONSTANT_ASCEN
    static CONSTANT_DESCENT
    static CENTER_OFFSET
    static OTHER
}

export interface PrintWriter {

}

export interface InputMethodEvent extends AWTEvent {

}

export interface AttributedCharacterIterator extends CharacterIterator {

}

export interface Attribute {
    hashCode(): number

    equals(arg0: any): boolean

    getName(): string

    toString(): string

    readResolve(): any
}

export interface CharacterIterator {

}


export interface KeyEvent extends InputEvent {

    getExtendedKeyCode(): number

    getKeyCode(): number

    isActionKey(): boolean

    getKeyChar(): string


    paramString(): string


    getKeyLocation(): number


    getKeyText(): string

    getKeyModifiersText(): string

    getExtendedKeyCodeForChar(): number

}

export interface InputMethodRequests {
}

export interface Toolkit {
}

export interface HierarchyListener extends EventListener {
}

export interface AccessControlContext {

    getAssignedCombiner(): DomainCombiner

    getDomainCombiner(): DomainCombiner

    getContext(): ProtectionDomain[]

    isPrivileged(accesscontrolcontext: AccessControlContext): boolean

    getCombiner(): DomainCombiner


    isAuthorized(): boolean


    getDebug(): Debug


}

export interface DropTarget {

}


export interface DomainCombiner {
    combine(arg0: ProtectionDomain[], arg1: ProtectionDomain[]): ProtectionDomain[]
}

export interface Debug {
    println()

    println(arg0: string)

    println(arg0: string, arg1: string)

    toString(arg0: number[]): string

    toHexString(biginteger: BigInteger): string

    getInstance(arg0: string): Debug

    getInstance(arg0: string, arg1: string): Debug

    isOn(arg0: string): boolean

    Help()

    marshal(arg0: string): string
}

export interface ProtectionDomain {

}


export interface HierarchyEvent extends AWTEvent {
    getChangedParent(): Container

    getComponent(): Component

    paramString(): string

    getChangeFlags(): number

    getChanged(): Component

    changed: Component;
    changedParent: Container;
    changeFlags: number;
}

export interface ComponentListener extends EventListener {

}

export interface ComponentEvent extends AWTEvent {

}

export interface MouseWheelListener extends EventListener {
    mouseWheelMoved(mousewheelevent: MouseWheelEvent)
}

export interface Cursor {
    getName(): string

    toString(): string

    getType(): number

    setPData(arg0: number)

    initCursorDir(): string

    getSystemCustomCursor(arg0: string): Cursor

    getDefaultCursor(): Cursor

    loadSystemCustomCursorProperties()

    getPredefinedCursor(arg0: number): Cursor

    type: number;
    name: string;
}

export interface Insets {

}

export interface AccessibleContext {
    getAccessibleRole(): AccessibleRole

    getAccessibleTable(): AccessibleTable

    getAccessibleParent(): Accessible

    getLocale(): Locale

    firePropertyChange(arg0: string, arg1: any, arg2: any)

    getAccessibleValue(): AccessibleValue

    getAccessibleComponent(): AccessibleComponent

    setAccessibleParent(accessible: Accessible)

    addPropertyChangeListener(propertychangelistener: PropertyChangeListener)

    getAccessibleIcon(): AccessibleIcon[]

    setAccessibleName(arg0: string)

    getAccessibleAction(): AccessibleAction

    getAccessibleStateSet(): AccessibleStateSet

    getAccessibleIndexInParent(): number

    getAccessibleChild(arg0: number): Accessible

    getAccessibleDescription(): string

    getAccessibleText(): AccessibleText

    getAccessibleName(): string

    getAccessibleChildrenCount(): number

    setAccessibleDescription(arg0: string)

    getAccessibleRelationSet(): AccessibleRelationSet

    getAccessibleEditableText(): AccessibleEditableText

    removePropertyChangeListener(propertychangelistener: PropertyChangeListener)

    getAccessibleSelection(): AccessibleSelection

    accessibleParent: Accessible;
    accessibleName: string;
    accessibleDescription: string;
}

export interface AccessibleState extends AccessibleBundle {

}

export interface AccessibleStateSet {

}

export interface AccessibleAction {

}

export interface AccessibleIcon {

}

export interface AccessibleRelationSet {

}

export interface AccessibleRelation extends AccessibleBundle {

}

export interface AccessibleBundle {

}

export interface AccessibleSelection {

}

export interface AccessibleText {
    getBeforeIndex(arg0: number, arg1: number): string

    getCharacterBounds(arg0: number): Rectangle

    getSelectionStart(): number

    getCharCount(): number

    getCharacterAttribute(arg0: number): AttributeSet

    getSelectedText(): string

    getSelectionEnd(): number

    getCaretPosition(): number

    getIndexAtPoint(point: Point): number

    getAtIndex(arg0: number, arg1: number): string

    getAfterIndex(arg0: number, arg1: number): string
}

export interface AttributeSet {

}

export interface Enumeration<E> {
    hasMoreElements(argument1: Enumeration<E>): boolean

    nextElement(argument1: Enumeration<E>): E
}

export interface AccessibleEditableText extends AccessibleText {

}

export interface AccessibleComponent {
    contains(point: Point): boolean

    setCursor(cursor: Cursor)

    setBounds(rectangle: Rectangle)

    getSize(): Dimension

    getBounds(): Rectangle

    setLocation(point: Point)

    getCursor(): Cursor

    addFocusListener(focuslistener: FocusListener)

    getBackground(): Color

    setEnabled(arg0: boolean)

    getLocationOnScreen(): Point

    setForeground(color: Color)

    getLocation(): Point

    isShowing(): boolean

    getFontMetrics(font: Font): FontMetrics

    setSize(dimension: Dimension)

    removeFocusListener(focuslistener: FocusListener)

    setVisible(arg0: boolean)

    getAccessibleAt(point: Point): Accessible

    isEnabled(): boolean

    isFocusTraversable(): boolean

    setFont(font: Font)

    getFont(): Font

    isVisible(): boolean

    getForeground(): Color

    requestFocus()

    setBackground(color: Color)
}

export interface PropertyChangeListener extends EventListener {
    propertyChange(propertychangeevent: PropertyChangeEvent)
}

export interface PropertyChangeEvent {

}

export interface FontMetrics {
}

export interface AccessibleValue {
    getMaximumAccessibleValue(): Number

    getCurrentAccessibleValue(): Number

    setCurrentAccessibleValue(number: Number): boolean

    getMinimumAccessibleValue(): Number
}

export interface Locale {

}

export interface Accessible {
    getAccessibleContext(): AccessibleContext
}

export interface AccessibleRole {

}

export interface AccessibleTable {

}

export interface Font {
}

export interface Graphics {
}

export interface BufferCapabilities {

}

export interface MouseListener {

}

export interface Color {

}

export interface RequestFocusController {

}

export interface JPopupMenu {
}

export interface Region {

}

export interface AWTEvent {

}

export interface AWTKeyStroke {

}

export interface ComponentOrientation {

}

export interface KeyListener {

}

export interface FocusListener {

}

export interface ComponentPeer {

}

export interface GraphicsConfiguration {

}

export interface ColorModel {

}

export interface Point {
    getLocation(): Point

    getX(): number

    equals(arg0: any): boolean

    getY(): number

    toString(): string

    setLocation(arg0: number, arg1: number)

    setLocation(arg0: number, arg1: number)

    setLocation(point: Point)

    translate(arg0: number, arg1: number)

    move(arg0: number, arg1: number)

    x: number;
    y: number;
}

export interface ComponentHierarchy {
    contains(): boolean

    roots(): Container[]

    parentOf(): Container

    childrenOf(): Component[]

    dispose(window: Window)
}

export interface Container {
}

export interface ComponentPrinter {
}

export interface Settings {
    timeoutToFindPopup(): number

    timeoutToFindPopup(arg0: number)

    robot(): Robot

    updateRobotAutoDelay()

    valueToUpdate(arg0: number, arg1: number, arg2: number): number

    eventPostingDelay(arg0: number)

    eventPostingDelay(): number

    idleTimeout(arg0: number)

    idleTimeout(): number

    clickOnDisabledComponentsAllowed(arg0: boolean)

    clickOnDisabledComponentsAllowed(): boolean

    attachTo(robot: Robot)

    dragButton(): MouseButton

    dragButton(mousebutton: MouseButton)

    simpleWaitForIdle(arg0: boolean)

    simpleWaitForIdle(): boolean

    delayBetweenEvents(): number

    delayBetweenEvents(arg0: number)

    dragDelay(): number

    dragDelay(arg0: number)

    timeoutToFindSubMenu(arg0: number)

    timeoutToFindSubMenu(): number

    dropDelay(): number

    dropDelay(arg0: number)

    timeoutToBeVisible(arg0: number)

    timeoutToBeVisible(): number

    shouldPreserveScreenshots(): boolean

    get(properties1, arg1: string, arg2: number): number

    get(properties1, arg1: string, arg2: boolean): boolean
}


export interface ComponentFinder {
    findAll(container: Container, componentmatcher: ComponentMatcher): Component[]

    findAll<T extends Component>(container: Container, arg1: GenericTypeMatcher<T>): T[]

    findAll(componentmatcher: ComponentMatcher): Component[]

    findAll<T extends Component>(arg0: GenericTypeMatcher<T>): T[]

    findByLabel<T extends Component>(container: Container, arg1: string, arg2: Class<T>): T

    findByLabel<T extends Component>(container: Container, arg1: string, arg2: Class<T>, arg3: boolean): T

    findByLabel(arg0: string): Component

    findByLabel<T extends Component>(arg0: string, arg1: Class<T>): T

    findByLabel<T extends Component>(arg0: string, arg1: Class<T>, arg2: boolean): T

    findByLabel(arg0: string, arg1: boolean): Component

    findByLabel(container: Container, arg1: string, arg2: boolean): Component

    findByLabel(container: Container, arg1: string): Component

    find(componentmatcher: ComponentMatcher): Component

    find<T extends Component>(arg0: GenericTypeMatcher<T>): T

    find<T extends Component>(container: Container, arg1: GenericTypeMatcher<T>): T

    find(container: Container, componentmatcher: ComponentMatcher): Component

    printer(): ComponentPrinter

    includeHierarchyIfComponentNotFound(): boolean

    includeHierarchyIfComponentNotFound(arg0: boolean)

    findByName<T extends Component>(container: Container, arg1: string, arg2: Class<T>): T

    findByName(container: Container, arg1: string, arg2: boolean): Component

    findByName<T extends Component>(container: Container, arg1: string, arg2: Class<T>, arg3: boolean): T

    findByName<T extends Component>(arg0: string, arg1: Class<T>, arg2: boolean): T

    findByName(arg0: string): Component

    findByName<T extends Component>(arg0: string, arg1: Class<T>): T

    findByName(arg0: string, arg1: boolean): Component

    findByName(container: Container, arg1: string): Component

    findByType<T extends Component>(container: Container, arg1: Class<T>, arg2: boolean): T

    findByType<T extends Component>(container: Container, arg1: Class<T>): T

    findByType<T extends Component>(arg0: Class<T>, arg1: boolean): T

    findByType<T extends Component>(arg0: Class<T>): T
}

export interface ComponentMatcher {
    matches(component: Component): boolean
}

export interface GenericTypeMatcher<T extends Component> extends AbstractComponentMatcher {
    isMatching(t: T): boolean

    supportedType()

    matches(component: Component): boolean
}

export interface AbstractComponentMatcher extends ResettableComponentMatcher {
    requireShowingMatches(component: Component): boolean

    reset(arg0: boolean)

    requireShowing(): boolean

    requireShowing(arg0: boolean)
}

export interface ResettableComponentMatcher extends ComponentMatcher {
    reset(arg0: boolean)
}

export interface Class<T> {
}

export interface Rectangle {
    contains(point: Point): boolean

    contains(rectangle2: Rectangle): boolean

    contains(arg0: number, arg1: number): boolean

    contains(arg0: number, arg1: number, arg2: number, arg3: number): boolean

    getY(): number

    setBounds(arg0: number, arg1: number, arg2: number, arg3: number)

    setBounds(rectangle2: Rectangle)

    getSize(): Dimension

    getBounds(): Rectangle

    setLocation(arg0: number, arg1: number)

    setLocation(point: Point)

    outcode(arg0: number, arg1: number): number

    add(arg0: number, arg1: number)

    add(rectangle2: Rectangle)

    add(point: Point)

    getLocation(): Point

    getX(): number

    getHeight(): number

    equals(arg0: any): boolean

    isEmpty(): boolean

    toString(): string

    resize(arg0: number, arg1: number)

    setSize(dimension: Dimension)

    setSize(arg0: number, arg1: number)

    inside(arg0: number, arg1: number): boolean

    grow(arg0: number, arg1: number)

    getWidth(): number

    translate(arg0: number, arg1: number)

    intersection(rectangle: Rectangle): Rectangle

    reshape(arg0: number, arg1: number, arg2: number, arg3: number)

    union(rectangle: Rectangle): Rectangle

    intersects(rectangle: Rectangle): boolean

    setRect(arg0: number, arg1: number, arg2: number, arg3: number)

    move(arg0: number, arg1: number)

    clip(arg0: number, arg1: boolean): number

    x: number;
    y: number;
    width: number;
    height: number;
}

export interface Image {
    getHeight(imageobserver: ImageObserver): number

    getSource(): ImageProducer

    setAccelerationPriority(arg0: number)

    flush()

    getAccelerationPriority(): number

    getWidth(imageobserver: ImageObserver): number

    getProperty(arg0: string, imageobserver: ImageObserver): any

    getCapabilities(graphicsconfiguration: GraphicsConfiguration): ImageCapabilities

    getScaledInstance(arg0: number, arg1: number, arg2: number): Image

    getGraphics(): Graphics

    accelerationPriority: number;
}

export interface ImageObserver {
}

export interface ImageProducer {

}

export interface ImageCapabilities {
}


export interface StringBuilder extends CharSequence {
    insert(arg0: number, arg1: string[], arg2: number, arg3: number): StringBuilder

    insert(arg0: number, arg1: number): StringBuilder

    insert(arg0: number, arg1: number): StringBuilder

    insert(arg0: number, arg1: number): StringBuilder

    insert(arg0: number, arg1: number): StringBuilder

    insert(arg0: number, arg1: any): StringBuilder

    insert(arg0: number, arg1: string): StringBuilder

    insert(arg0: number, arg1: string[]): StringBuilder

    insert(arg0: number, charsequence: CharSequence): StringBuilder

    insert(arg0: number, charsequence: CharSequence, arg2: number, arg3: number): StringBuilder

    insert(arg0: number, arg1: boolean): StringBuilder

    insert(arg0: number, arg1: string): StringBuilder

    append(arg0: boolean): StringBuilder

    append(charsequence: CharSequence): StringBuilder

    append(arg0: string): StringBuilder

    append(arg0: number): StringBuilder

    append(stringbuffer: StringBuffer): StringBuilder

    append(arg0: string[]): StringBuilder

    append(charsequence: CharSequence, arg1: number, arg2: number): StringBuilder

    append(arg0: number): StringBuilder

    append(arg0: string[], arg1: number, arg2: number): StringBuilder

    append(arg0: string): StringBuilder

    append(arg0: any): StringBuilder

    append(arg0: number): StringBuilder

    append(arg0: number): StringBuilder

    deleteCharAt(arg0: number): StringBuilder

    replace(arg0: number, arg1: number, arg2: string): StringBuilder

    appendCodePoint(arg0: number): StringBuilder

    delete(arg0: number, arg1: number): StringBuilder

    reverse(): StringBuilder

    indexOf(arg0: string, arg1: number): number

    indexOf(arg0: string): number

    lastIndexOf(arg0: string, arg1: number): number

    lastIndexOf(arg0: string): number

    toString(): string

    readObject(objectinputstream: ObjectInputStream)

    writeObject(objectoutputstream: ObjectOutputStream)
}

export interface CharSequence {

}

export interface ObjectInputStream {

}

export interface ObjectOutputStream {

}

export interface StringBuffer {

}

export interface InputContext {

}