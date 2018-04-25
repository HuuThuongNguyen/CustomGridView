package com.huuthuong.jfx8.gui.helpers;


import com.google.common.collect.ImmutableMap;
import com.huuthuong.jfx8.gui.controllers.CustomGridViewController;
import com.sun.javafx.scene.control.skin.VirtualScrollBar;
import impl.org.controlsfx.skin.GridViewSkin;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.util.Pair;
import org.controlsfx.control.GridView;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class CustomGridView<T> extends GridView<T> {
    private CustomGridViewSkin gvContactsSkin;
    private Consumer<T> _UpdateSelection;

    protected Skin<? extends T> createDefaultSkin() {
        if (gvContactsSkin == null) {
            gvContactsSkin = new CustomGridViewSkin<T>(this);
        }
        return gvContactsSkin;
    }

    public VirtualScrollBar getVBar() {
        return gvContactsSkin.vBar;
    }

    public void setUpdateSelection(Consumer<T> callback) {
        _UpdateSelection = callback;
    }

    public void moveNext(@Nonnull KeyEvent keyEvent, T obj) {
        gvContactsSkin.moveNext(keyEvent, obj);
        requestFocus();
    }

    private class CustomGridViewSkin<E> extends GridViewSkin<E> {
        private int rows;
        private int maxCells;
        private VirtualScrollBar vBar;

        private Map<KeyCombination, Pair<Predicate<KeyEvent>, Consumer<E>>> mapNavigation = ImmutableMap.<KeyCombination, Pair<Predicate<KeyEvent>, Consumer<E>>>builder()
                .put(CustomGridViewController.COMBINATION_DOWN,
                        new Pair<>(CustomGridViewController.COMBINATION_DOWN::match, obj -> {
                            if (obj == null) {
                                selectFirst();
                                return;
                            }
                            selectDown(obj);
                        }))
                .put(CustomGridViewController.COMBINATION_UP,
                        new Pair<>(CustomGridViewController.COMBINATION_UP::match, obj -> {
                            if (obj != null) {
                                selectUp(obj);
                            }
                        }))
                .put(CustomGridViewController.COMBINATION_LEFT,
                        new Pair<>(CustomGridViewController.COMBINATION_LEFT::match, obj -> {
                            if (obj != null) {
                                selectLeft(obj);
                            }
                        }))
                .put(CustomGridViewController.COMBINATION_RIGHT,
                        new Pair<>(CustomGridViewController.COMBINATION_RIGHT::match, obj -> {
                            if (obj != null) {
                                selectRight(obj);
                            }
                        }))
                .build();



        public CustomGridViewSkin(GridView<E> control) {
            super(control);
            getVerticalBar();
        }

        public void updateRowCount() {
            super.updateRowCount();
            rows = getItemCount();
            maxCells = computeMaxCellsInRow();
        }

        private VirtualScrollBar getVerticalBar() {
            for (Node child : flow.getChildrenUnmodifiable()) {
                if (child instanceof VirtualScrollBar) {
                    if (((VirtualScrollBar) child).getOrientation() == Orientation.VERTICAL) {
                        vBar = (VirtualScrollBar) child;
                        break;
                    }
                }
            }
            return vBar;
        }

        public void moveNext(KeyEvent keyEvent, E obj) {
            for (Map.Entry<KeyCombination, Pair<Predicate<KeyEvent>, Consumer<E>>> entry : this.mapNavigation.entrySet()) {
                Pair<Predicate<KeyEvent>, Consumer<E>> pairProcessEvent = entry.getValue();
                if (pairProcessEvent.getKey().test(keyEvent)) {
                    if (pairProcessEvent.getValue() != null)
                        pairProcessEvent.getValue().accept(obj);
                    keyEvent.consume();
                    break;
                }
            }
        }

        private void scrollTo(ObservableList<T> items, int currIdx) {
            int rowIdx = (int) Math.floor((double)currIdx / (double)maxCells);
            flow.scrollTo(rowIdx);
            if (_UpdateSelection != null)
                _UpdateSelection.accept(items.get(currIdx));
        }

        private void selectFirst() {
            ObservableList items = CustomGridView.this.getItems();
            if (!items.isEmpty())
                scrollTo(items, 0);
        }

        private void selectLeft(E obj) {
            ObservableList items = CustomGridView.this.getItems();
            int currIdx = items.indexOf(obj);
            if (currIdx > 0) {
                int prevIdx = currIdx - 1;
                if (prevIdx >= 0) {
                    scrollTo(items, prevIdx);
                }
            }
        }

        private void selectRight(E obj) {
            ObservableList items = CustomGridView.this.getItems();
            int currIdx = items.indexOf(obj);
            int size = items.size();
            if (currIdx < size - 1) {
                int nextIdx = currIdx + 1;
                if (nextIdx < size) {
                    scrollTo(items, nextIdx);
                }
            }
        }

        private void selectUp(E obj) {
            ObservableList items = CustomGridView.this.getItems();
            int currIdx = items.indexOf(obj);
            if (currIdx > 0) {
                int upIdx = currIdx - maxCells;
                if (upIdx >= 0)
                    scrollTo(items, upIdx);
            }
        }

        private void selectDown(E obj) {
            ObservableList items = CustomGridView.this.getItems();
            int currIdx = items.indexOf(obj);
            int size = items.size();
            if (currIdx < size - 1) {
                int downIdx = currIdx + maxCells;
                if (downIdx < size) {
                    scrollTo(items, downIdx);
                }
                else {
                    int rowIdx = (int) Math.floor((double)downIdx / (double)maxCells);
                    if (rowIdx == rows) {
                        downIdx = (maxCells * (rowIdx - 1)) + 1;
                        scrollTo(items, downIdx);
                    }
                }
            }
        }
    }
}
