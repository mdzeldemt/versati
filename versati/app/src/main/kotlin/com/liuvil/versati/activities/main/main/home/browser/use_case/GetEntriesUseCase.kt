package com.liuvil.versati.activities.main.main.home.browser.use_case

import com.liuvil.versati.activities.main.main.home.RepositoryFactory
import com.liuvil.versati.activities.main.main.home.browser.Entry
import com.liuvil.versati.activities.main.main.home.browser.Source
import com.liuvil.versati.framework.html.extractImageUrls
import com.liuvil.versati.repository.api.data.EntryStatus
import org.jsoup.Jsoup
import javax.inject.Inject

internal class GetEntriesUseCase @Inject constructor(
    private val repositoryFactory: RepositoryFactory
) {
    suspend operator fun invoke(
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
                val document = Jsoup.parse(entry.content)
                val text = document.text()
                val imageUrl =
                    extractImageUrls(document).firstOrNull()
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