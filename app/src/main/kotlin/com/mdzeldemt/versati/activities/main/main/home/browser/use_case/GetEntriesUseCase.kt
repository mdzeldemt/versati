package com.mdzeldemt.versati.activities.main.main.home.browser.use_case

import com.mdzeldemt.versati.activities.main.main.home.RepositoryFactory
import com.mdzeldemt.versati.activities.main.main.home.browser.Entry
import com.mdzeldemt.versati.activities.main.main.home.browser.Source
import com.mdzeldemt.versati.framework.html.parse.HtmlBlock
import com.mdzeldemt.versati.framework.html.parse.descendants
import com.mdzeldemt.versati.framework.html.parse.parseDocument
import com.mdzeldemt.versati.repository.api.data.EntryStatus
import okhttp3.HttpUrl.Companion.toHttpUrl
import javax.inject.Inject

internal class GetEntriesUseCase @Inject constructor(
    private val repositoryFactory: RepositoryFactory
) {
    suspend fun perform(
        source: Source,
        offset: Int,
        limit: Int
    ): Result<Pair<List<Entry>, Int>> {
        val repository = repositoryFactory.create()

        return runCatching {
            val response = when (source) {
                is Source.Unread ->
                    repository.getAllEntries(
                        read = false,
                        offset = offset,
                        globallyVisible = true,
                        limit = limit
                    )

                is Source.History ->
                    repository.getAllEntries(
                        read = true,
                        offset = offset,
                        limit = limit
                    )

                is Source.Starred ->
                    repository.getAllEntries(
                        starred = true,
                        offset = offset,
                        limit = limit
                    )

                is Source.Category ->
                    repository.getEntriesFromCategory(
                        categoryId = source.id,
                        read = false,
                        offset = offset,
                        limit = limit
                    )

                is Source.Feed ->
                    repository.getEntriesFromFeed(
                        feedId = source.id,
                        read = false,
                        offset = offset,
                        limit = limit
                    )

                is Source.Search ->
                    repository.getAllEntries(
                        search = source.term,
                        offset = offset,
                        limit = limit
                    )
            }

            val mapped = response.entries.map { entry ->
                val content = parseDocument(entry.content).descendants()
                val text = content.filterIsInstance<HtmlBlock.Paragraph>()
                    .joinToString(separator = "\n") { it.text.toString() }
                val imageUrl = content.filterIsInstance<HtmlBlock.Image>().firstOrNull()?.url?.toHttpUrl()?.toUrl()
                    ?: entry.enclosures.firstOrNull()?.url

                Entry(
                    id = entry.id,
                    title = entry.title,
                    url = entry.url,
                    feedId = entry.feedId,
                    isRead = entry.status == EntryStatus.READ,
                    text = text,
                    imageUrl = imageUrl,
                    publishedAt = entry.publishedAt
                )
            }

            mapped to response.total
        }
    }
}