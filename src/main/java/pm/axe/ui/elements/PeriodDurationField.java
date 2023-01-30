package pm.axe.ui.elements;

import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.IntegerField;
import org.threeten.extra.PeriodDuration;
import pm.axe.Axe;

import java.time.Duration;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static java.time.temporal.ChronoUnit.*;

public class PeriodDurationField extends CustomField<PeriodDuration> {
    public static final String ERROR_MESSAGE = "Invalid duration. Min: 1 minute, Max: 6 months.";
    private static final List<ChronoUnit> SUPPORTED_UNITS = List.of(MONTHS, DAYS, HOURS, MINUTES);

    private final IntegerField amount;
    private final Select<String> unit;

    public PeriodDurationField() {
        //amount field
        amount = new IntegerField();
        amount.setValue(1);
        amount.setMin(1);
        amount.setMax(6); //max varies per unit
        amount.setStepButtonsVisible(true);
        amount.addValueChangeListener(this::onAmountChanged);

        //unit select
        unit = new Select<>();
        unit.setItems(getUnits());
        unit.setValue(MONTHS.name());
        unit.addValueChangeListener(this::onUnitChanged);

        //styles
        amount.setWidth("8em");
        unit.setWidth("8em");

        // aria-label for screen readers
        amount.getElement()
                .executeJs("const amount = this.inputElement;"
                        + "amount.setAttribute('aria-label', 'Number');"
                        + "amount.removeAttribute('aria-labelledby');");
        unit.getElement()
                .executeJs("const unit = this.focusElement;"
                        + "unit.setAttribute('aria-label', 'Unit');"
                        + "unit.removeAttribute('aria-labelledby');");

        HorizontalLayout layout = new HorizontalLayout(amount, unit);
        // Removes default spacing
        layout.setSpacing(false);
        // Adds small amount of space between the components
        layout.getThemeList().add("spacing-s");

        add(layout);
    }

    public boolean isValid() {
        if (amount.isInvalid()) return false;
        return !unit.isInvalid();
    }

    private void onUnitChanged(final ComponentValueChangeEvent<Select<String>, String> event) {
        ChronoUnit chronoUnit = null;
        try {
            chronoUnit = ChronoUnit.valueOf(unit.getValue());
        } catch (IllegalArgumentException e) {
            unit.setInvalid(true);
            unit.setErrorMessage("Should be valid " + ChronoUnit.class.getSimpleName() + " constant.");
        } catch (NullPointerException npe) {
            unit.setInvalid(true);
            unit.setErrorMessage("Unit cannot be NULL");
        }
        if (isSupported(chronoUnit)) {
            switch (chronoUnit) {
                //max 6 months
                case MONTHS -> amount.setMax(6);
                case DAYS -> amount.setMax(182);
                case HOURS -> amount.setMax(4383);
                case MINUTES -> amount.setMax(263000);
                default -> {
                    amount.setMax(1);
                    amount.setEnabled(false);
                }
            }
            if (amount.getValue() > amount.getMax()) {
                amount.setValue(amount.getMax());
            }
        } else {
            unit.setInvalid(true);
            unit.setErrorMessage("Unit unsupported");
        }
    }

    private void onAmountChanged(final ComponentValueChangeEvent<IntegerField, Integer> e) {
        if (amount.isInvalid()) {
            this.setInvalid(true);
            this.setErrorMessage(ERROR_MESSAGE);
        } else {
            this.setInvalid(false);
        }
    }

    @Override
    protected PeriodDuration generateModelValue() {
        ChronoUnit chronoUnit;
        try {
            chronoUnit = ChronoUnit.valueOf(unit.getValue());
        } catch (IllegalArgumentException e) {
            unit.setInvalid(true);
            unit.setErrorMessage("Should be valid " + ChronoUnit.class.getSimpleName() + " constant.");
            return PeriodDuration.ZERO;
        } catch (NullPointerException npe) {
            unit.setInvalid(true);
            unit.setErrorMessage("Unit cannot be NULL");
            return PeriodDuration.ZERO;
        }

        if (isSupported(chronoUnit)) {
            if (chronoUnit.isTimeBased()) {
                return PeriodDuration.of(Duration.of(amount.getValue(), chronoUnit));
            } else {
                return switch (chronoUnit) {
                    case DAYS -> PeriodDuration.of(Period.ofDays(amount.getValue()));
                    case MONTHS -> PeriodDuration.of(Period.ofMonths(amount.getValue()));
                    default -> PeriodDuration.ZERO; //all others are unsupported
                };
            }
        } else {
            unit.setInvalid(true);
            unit.setErrorMessage("Unit unsupported");
            return PeriodDuration.ZERO;
        }
    }

    @Override
    protected void setPresentationValue(PeriodDuration periodDuration) {
        if (periodDuration.isZero()) {
            //zero - set default
            periodDuration = Axe.Defaults.LOGIN_SESSION_DURATION;
        }

        if (periodDuration.getDuration().isZero()) {
            //period
            if (periodDuration.getPeriod().getMonths() > 0) {
                amount.setValue(periodDuration.getPeriod().getMonths());
                unit.setValue(MONTHS.name());
            } else {
                amount.setValue(periodDuration.getPeriod().getDays());
                unit.setValue(DAYS.name());
            }
        } else {
            //duration
            if (periodDuration.getDuration().toHoursPart() > 0) {
                amount.setValue(periodDuration.getDuration().toHoursPart());
                unit.setValue(HOURS.name());
            } else {
                amount.setValue(periodDuration.getDuration().toMinutesPart());
                unit.setValue(MINUTES.name());
            }
        }
    }

    private boolean isSupported(final ChronoUnit chronoUnit) {
        if (chronoUnit == null) return false;
        for (ChronoUnit u: SUPPORTED_UNITS) {
            if (u.equals(chronoUnit)) {
                return true;
            }
        }
        return false;
    }

    private List<String> getUnits() {
       return SUPPORTED_UNITS.stream().map(ChronoUnit::name).toList();
    }
}
