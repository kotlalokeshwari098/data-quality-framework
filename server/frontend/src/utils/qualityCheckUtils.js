/**
 * Utility functions for working with quality checks and results
 */

/**
 * Check status enumeration
 */
export const CheckStatus = {
  PASSED: 'PASSED',
  WARNING: 'WARNING',
  FAILED: 'FAILED',
  UNKNOWN: 'UNKNOWN',
  NO_DATA: 'NO DATA'
}

/**
 * Get the status of a quality check result
 * @param {Object} result - The check result object
 * @param {Object} check - The quality check definition
 * @returns {string} The status (PASSED, WARNING, FAILED, UNKNOWN)
 */
export function getCheckStatus(result, check) {
  if (!check) return CheckStatus.UNKNOWN

  const errorThreshold = check.errorThreshold
  const warningThreshold = check.warningThreshold

  // Failed: result > errorThreshold (too many problems)
  if (result.result > errorThreshold) {
    return CheckStatus.FAILED
  }
  // Warning: result > warningThreshold but <= errorThreshold
  else if (result.result > warningThreshold) {
    return CheckStatus.WARNING
  }
  // Passed: result <= warningThreshold (acceptable level)
  else {
    return CheckStatus.PASSED
  }
}

/**
 * Get the badge class for a check status
 * @param {string} status - The check status
 * @returns {string} Bootstrap badge class
 */
export function getStatusBadgeClass(status) {
  switch (status) {
    case CheckStatus.PASSED:
      return 'bg-success'
    case CheckStatus.FAILED:
      return 'bg-danger'
    case CheckStatus.WARNING:
      return 'bg-warning text-dark'
    default:
      return 'bg-secondary'
  }
}

/**
 * Get the color class for a progress indicator
 * @param {Object} result - The check result object
 * @param {Object} check - The quality check definition
 * @returns {string} CSS classes for progress color
 */
export function getProgressColorClass(result, check) {
  if (!check) return 'border-secondary bg-secondary'

  const status = getCheckStatus(result, check)

  switch (status) {
    case CheckStatus.FAILED:
      return 'border-danger bg-danger'
    case CheckStatus.WARNING:
      return 'border-warning bg-warning'
    case CheckStatus.PASSED:
      return 'border-success bg-success'
    default:
      return 'border-secondary bg-secondary'
  }
}

/**
 * Count checks by status in a report
 * @param {Object} report - The report object
 * @param {Map} qualityCheckMap - Map of check hash -> check definition
 * @returns {Object} Counts object with passed, warning, and failed properties
 */
export function countChecksByStatus(report, qualityCheckMap) {
  const counts = {
    passed: 0,
    warnings: 0,
    failed: 0,
    total: 0
  }

  if (!report.results || !Array.isArray(report.results)) {
    return counts
  }

  counts.total = report.results.length

  report.results.forEach(result => {
    const check = qualityCheckMap.get(result.hash)
    if (!check) return

    const status = getCheckStatus(result, check)

    switch (status) {
      case CheckStatus.PASSED:
        counts.passed++
        break
      case CheckStatus.WARNING:
        counts.warnings++
        break
      case CheckStatus.FAILED:
        counts.failed++
        break
    }
  })

  return counts
}

/**
 * Get the overall status of a report
 * @param {Object} report - The report object
 * @param {Map} qualityCheckMap - Map of check hash -> check definition
 * @returns {string} Overall report status
 */
export function getReportStatus(report, qualityCheckMap) {
  if (!report.results || report.results.length === 0) {
    return CheckStatus.NO_DATA
  }

  let hasError = false
  let hasWarning = false

  report.results.forEach(result => {
    const check = qualityCheckMap.get(result.hash)
    if (!check) return

    const status = getCheckStatus(result, check)

    if (status === CheckStatus.FAILED) {
      hasError = true
    } else if (status === CheckStatus.WARNING) {
      hasWarning = true
    }
  })

  if (hasError) return CheckStatus.FAILED
  if (hasWarning) return CheckStatus.WARNING
  return CheckStatus.PASSED
}

/**
 * Format a score as a percentage
 * @param {number} score - The score value (0-1)
 * @returns {string} Formatted percentage string
 */
export function formatScore(score) {
  return (score * 100).toFixed(1) + '%'
}

/**
 * Format a score as a rounded percentage for display
 * @param {number} score - The score value (0-1)
 * @returns {number} Rounded percentage
 */
export function formatScoreRounded(score) {
  return Math.round(score * 100)
}

