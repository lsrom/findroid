package dev.jdtech.jellyfin.tv.ui

import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.leanback.widget.Presenter
import dev.jdtech.jellyfin.R
import dev.jdtech.jellyfin.databinding.BaseItemBinding
import dev.jdtech.jellyfin.databinding.HomeEpisodeItemBinding
import org.jellyfin.sdk.model.api.BaseItemDto

class MediaItemPresenter(private val onClick: (BaseItemDto) -> Unit) : Presenter() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val mediaView =
            BaseItemBinding
                .inflate(parent.context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater)
                .root
        return ViewHolder(mediaView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        if (item is BaseItemDto) {
            DataBindingUtil.getBinding<BaseItemBinding>(viewHolder.view)?.apply {
                this.item = item
                this.itemName.text = if (item.type == "Episode") item.seriesName else item.name
                this.itemCount.visibility =
                    if (item.userData?.unplayedItemCount != null && item.userData?.unplayedItemCount!! > 0) View.VISIBLE else View.GONE
                this.itemLayout.layoutParams.width =
                    this.itemLayout.resources.getDimension(R.dimen.overview_media_width).toInt()
                (this.itemLayout.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin = 0
                viewHolder.view.setOnClickListener { onClick(item) }
            }
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) = Unit
}

class DynamicMediaItemPresenter(private val onClick: (BaseItemDto) -> Unit) : Presenter() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val mediaView =
            HomeEpisodeItemBinding
                .inflate(parent.context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater)
                .root
        return ViewHolder(mediaView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        if (item is BaseItemDto) {
            DataBindingUtil.getBinding<HomeEpisodeItemBinding>(viewHolder.view)?.apply {
                episode = item
                item.userData?.playedPercentage?.toInt()?.let {
                    progressBar.layoutParams.width = TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        (it.times(2.24)).toFloat(), progressBar.context.resources.displayMetrics).toInt()
                    progressBar.isVisible = true
                }

                if (item.type == "Movie") {
                    primaryName.text = item.name
                    secondaryName.visibility = View.GONE
                } else if (item.type == "Episode") {
                    primaryName.text = item.seriesName
                }

                viewHolder.view.setOnClickListener { onClick(item) }
            }
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) = Unit
}