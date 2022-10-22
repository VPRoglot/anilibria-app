package ru.radiationx.anilibria.ui.fragments.donation.yoomoney

import android.graphics.Rect
import android.os.Bundle
import android.text.method.TransformationMethod
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import kotlinx.android.synthetic.main.dialog_donation_yoomoney.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.radiationx.anilibria.R
import ru.radiationx.anilibria.presentation.donation.infra.DonationYooMoneyState
import ru.radiationx.anilibria.presentation.donation.yoomoney.DonationYooMoneyPresenter
import ru.radiationx.anilibria.presentation.donation.yoomoney.DonationYooMoneyView
import ru.radiationx.anilibria.ui.fragments.AlertDialogFragment
import ru.radiationx.data.entity.app.donation.content_data.YooMoneyDialogResponse
import ru.radiationx.data.entity.domain.donation.yoomoney.YooMoneyDialog
import ru.radiationx.shared.ktx.android.addTextChangeListener
import ru.radiationx.shared.ktx.android.bindOptionalViews
import ru.radiationx.shared_app.di.getDependency

class DonationYooMoneyDialogFragment :
    AlertDialogFragment(R.layout.dialog_donation_yoomoney), DonationYooMoneyView {

    @InjectPresenter
    lateinit var presenter: DonationYooMoneyPresenter

    @ProvidePresenter
    fun providePresenter(): DonationYooMoneyPresenter =
        getDependency(DonationYooMoneyPresenter::class.java)

    override fun onStart() {
        super.onStart()
        getAlertDialog()?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        yooMoneyAmounts.addCheckedListener { checkedId ->
            val intValue = checkedId
                ?.let { yooMoneyAmounts.findViewById<MaterialButton>(it) }
                ?.getTextIntValue()
            presenter.setSelectedAmount(intValue)
        }
        yooMoneyAmountField.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                val intValue = yooMoneyAmountField.getTextIntValue()
                presenter.setCustomAmount(intValue)
            }
        }

        yooMoneyAmountField.addTextChangeListener {
            val intValue = yooMoneyAmountField.getTextIntValue()
            presenter.setCustomAmount(intValue)
        }

        yooMoneyTypes.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked && checkedId != View.NO_ID) {
                val paymentTypeId = when (checkedId) {
                    R.id.yooMoneyTypeAccount -> YooMoneyDialog.TYPE_ID_ACCOUNT
                    R.id.yooMoneyTypeCard -> YooMoneyDialog.TYPE_ID_CARD
                    R.id.yooMoneyTypeMobile -> YooMoneyDialog.TYPE_ID_MOBILE
                    else -> null
                }
                if (paymentTypeId != null) {
                    presenter.setPaymentType(paymentTypeId)
                }
            }
        }

        yooMoneyAccept.setOnClickListener {
            presenter.onAcceptClick()
        }

        yooMoneyCancel.setOnClickListener {
            dismiss()
        }
    }

    override fun close() {
        dismiss()
    }

    override fun showData(state: DonationYooMoneyState) {
        state.data?.also { bindData(it) }
        bindState(state)
    }

    private fun bindState(state: DonationYooMoneyState) {
        when (state.amountType) {
            DonationYooMoneyState.AmountType.PRESET -> {
                if (state.selectedAmount != null) {
                    yooMoneyAmounts.check(state.selectedAmount)
                } else {
                    yooMoneyAmounts.clearChecked()
                }
                yooMoneyAmountField.clearFocus()
            }
            DonationYooMoneyState.AmountType.CUSTOM -> {
                yooMoneyAmounts.clearChecked()
                yooMoneyAmountField.requestFocus()
            }
        }

        val selectedTypeViewId = when (state.selectedPaymentTypeId) {
            YooMoneyDialog.TYPE_ID_ACCOUNT -> R.id.yooMoneyTypeAccount
            YooMoneyDialog.TYPE_ID_CARD -> R.id.yooMoneyTypeCard
            YooMoneyDialog.TYPE_ID_MOBILE -> R.id.yooMoneyTypeMobile
            else -> null
        }
        if (selectedTypeViewId != null) {
            yooMoneyTypes.check(selectedTypeViewId)
        } else {
            yooMoneyTypes.clearChecked()
        }

        yooMoneyAccept.isEnabled = state.acceptEnabled
    }

    private fun bindData(data: YooMoneyDialog) {
        yooMoneyTitle.text = data.title

        if (data.help != null) {
            yooMoneyHelp.text = data.help
            yooMoneyTitle.setOnClickListener {
                presenter.submitHelpClickAnalytics()
                yooMoneyHelp.isVisible = !yooMoneyHelp.isVisible
            }
        } else {
            yooMoneyHelp.isVisible = false
            yooMoneyTitle.setOnClickListener(null)
        }

        val amountViews = listOf<View>(
            yooMoneyAmountTitle,
            yooMoneyAmounts,
            yooMoneyAmountInput
        )
        data.amounts.bindOptionalViews(amountViews) {
            yooMoneyAmountTitle.text = it.title
            yooMoneyAmountInput.hint = it.hint
            updateAmountsViews(it.items)
        }

        data.paymentTypes.also { types ->
            yooMoneyTypeTitle.text = types.title

            val accountViews = listOf<View>(yooMoneyTypeAccount, yooMoneyTypeAccountName)
            val cardViews = listOf<View>(yooMoneyTypeCard, yooMoneyTypeCardName)
            val mobileViews = listOf<View>(yooMoneyTypeMobile, yooMoneyTypeMobileName)

            types.items
                .firstOrNull { it.id == YooMoneyDialog.TYPE_ID_ACCOUNT }
                .bindOptionalViews(accountViews) {
                    yooMoneyTypeAccountName.text = it.title
                }
            types.items
                .firstOrNull { it.id == YooMoneyDialog.TYPE_ID_CARD }
                .bindOptionalViews(cardViews) {
                    yooMoneyTypeCardName.text = it.title
                }
            types.items
                .firstOrNull { it.id == YooMoneyDialog.TYPE_ID_MOBILE }
                .bindOptionalViews(mobileViews) {
                    yooMoneyTypeMobileName.text = it.title
                }

            val hasSupportedTypes = types.items.any {
                it.id == YooMoneyDialog.TYPE_ID_ACCOUNT ||
                        it.id == YooMoneyDialog.TYPE_ID_CARD ||
                        it.id == YooMoneyDialog.TYPE_ID_MOBILE
            }
            yooMoneyTypeTitle.isVisible = hasSupportedTypes
            yooMoneyTypes.isVisible = hasSupportedTypes
            yooMoneyTypesNames.isVisible = hasSupportedTypes
        }

        yooMoneyAccept.text = data.btDonateText
        yooMoneyCancel.text = data.btCancelText
    }

    private fun updateAmountsViews(amounts: List<Int>) {
        if (yooMoneyAmounts.childCount != amounts.size) {
            yooMoneyAmounts.clearChecked()
            yooMoneyAmounts.removeAllViews()

            amounts.forEach {
                val button = MaterialButton(
                    yooMoneyAmounts.context,
                    null,
                    R.attr.materialButtonOutlinedStyle
                )
                button.setPadding(0, 0, 0, 0)
                val layoutParams = LinearLayout.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    1f
                )
                button.transformationMethod = AmountCurrencyTransformation()
                yooMoneyAmounts.addView(button, layoutParams)
            }

        }
        amounts.forEachIndexed { index, amount ->
            (yooMoneyAmounts.getChildAt(index) as MaterialButton?)?.also {
                it.id = amount
                it.text = amount.toString()
            }
        }
    }

    override fun setRefreshing(refreshing: Boolean) {
        yooMoneyProgress.isVisible = refreshing
        yooMoneyAccept.isVisible = !refreshing
    }

    private fun TextView.getTextIntValue(): Int? = text?.toString()?.toIntOrNull()

    private fun MaterialButtonToggleGroup.addCheckedListener(action: (checkedId: Int?) -> Unit) {
        var lastValue: Int? = null
        addOnButtonCheckedListener { group, checkedId, isChecked ->
            val isSameValue = group.checkedButtonIds.size == 1
                    && group.checkedButtonIds[0] == group.checkedButtonId
            val isNoSelection = group.checkedButtonIds.isEmpty()
            if (isSameValue || isNoSelection) {
                val newValue = if (isNoSelection) null else group.checkedButtonId
                if (newValue != lastValue) {
                    lastValue = newValue
                    action.invoke(lastValue)
                }
            }
        }
    }

    private class AmountCurrencyTransformation : TransformationMethod {

        override fun getTransformation(source: CharSequence?, view: View?): CharSequence {
            return source?.toString().orEmpty() + " ₽"
        }

        override fun onFocusChanged(p0: View?, p1: CharSequence?, p2: Boolean, p3: Int, p4: Rect?) {
        }
    }
}